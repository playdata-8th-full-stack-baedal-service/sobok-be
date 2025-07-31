package com.sobok.userservice.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sobok.userservice.common.dto.TokenUserInfo;
import com.sobok.userservice.common.exception.CustomException;
import com.sobok.userservice.user.client.ApiServiceClient;
import com.sobok.userservice.user.client.CookServiceClient;
import com.sobok.userservice.user.client.PostServiceClient;
import com.sobok.userservice.user.dto.email.UserEmailDto;
import com.sobok.userservice.user.dto.info.AuthUserInfoResDto;
import com.sobok.userservice.user.dto.info.UserAddressDto;
import com.sobok.userservice.user.dto.payment.CartStartPayDto;
import com.sobok.userservice.user.dto.request.*;
import com.sobok.userservice.user.dto.response.*;
import com.sobok.userservice.user.entity.UserAddress;
import com.sobok.userservice.user.entity.UserBookmark;
import com.sobok.userservice.user.entity.UserLike;
import com.sobok.userservice.user.repository.UserAddressRepository;
import com.sobok.userservice.user.repository.UserBookmarkRepository;
import com.sobok.userservice.user.repository.UserLikeRepository;
import com.sobok.userservice.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sobok.userservice.user.entity.User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserAddressService userAddressService;
    private final UserAddressRepository userAddressRepository;
    private final UserBookmarkRepository userBookmarkRepository;
    private final CookServiceClient cookServiceClient;
    private final ApiServiceClient apiServiceClient;
    private final PostServiceClient postServiceClient;
    private final UserLikeRepository userLikeRepository;

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${cloudfront.alter}")
    private String ALTER_URL;

    public UserResDto findByPhoneNumber(String phoneNumber) {
        Optional<User> byPhone = userRepository.findByPhone(phoneNumber);
        if (byPhone.isPresent()) {
            User user = byPhone.get();
            log.info("전화번호로 얻어온 auth의 정보: {}", byPhone);
            return UserResDto.builder()
                    .id(user.getId())
                    .authId(user.getAuthId())
                    .nickname(user.getNickname())
                    .photo(user.getPhoto())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .build();

        } else {
            log.info("해당 번호로 가입하신 정보가 없습니다.");
            return null;
        }
    }

    /**
     * <pre>
     *     # 사용자 회원가입
     *     1. 사용자 객체 생성 후 저장
     *     2. 주소 값이 전달되었다면 사용자 주소도 저장
     * </pre>
     */
    @Transactional
    public void signup(UserSignupReqDto reqDto) {
        log.info("사용자 회원가입 시작 : {}", reqDto.getAuthId());

        String photoUrl = reqDto.getPhoto() == null ? ALTER_URL + "profile/default_profile.png" : reqDto.getPhoto();

        // 유저 객체 생성
        User user = User.builder()
                .authId(reqDto.getAuthId())
                .nickname(reqDto.getNickname())
                .phone(reqDto.getPhone())
                .photo(photoUrl)
                .email(reqDto.getEmail())
                .build();

        // user DB에 저장
        userRepository.save(user);

        // 사용자 주소 저장
        if (reqDto.getRoadFull() != null) {
            UserAddressReqDto addrDto = UserAddressReqDto.builder()
                    .roadFull(reqDto.getRoadFull())
                    .addrDetail(reqDto.getAddrDetail())
                    .build();

            userAddressService.addAddress(reqDto.getAuthId(), addrDto);
            log.info("성공적으로 사용자의 주소를 저장했습니다.");
        }

        log.info("성공적으로 사용자 회원가입이 완료되었습니다.");

    }

    /**
     * 사용자 정보 조회
     * 1. User 가져오기
     * 2. 주소 가져오기
     * 3. dto로 변환 (loginId는 없음)
     */
    public AuthUserInfoResDto getUserInfo(Long authId) {
        log.info("사용자 정보 조회 시작 : {}", authId);

        User user = userRepository.findByAuthId(authId).orElseThrow(
                () -> new CustomException("Auth ID가 존재하지 않습니다.", HttpStatus.NOT_FOUND)
        );

        List<UserAddressDto> userAddress =
                userAddressRepository.findByActiveUserId(user.getId())
                        .stream()
                        .map(address -> new UserAddressDto(address.getId(), address.getRoadFull(), address.getAddrDetail()))
                        .toList();

        if (userAddress.isEmpty()) {
            userAddress = null;
        }

        return AuthUserInfoResDto.builder()
                .loginId(null)
                .nickname(user.getNickname())
                .phone(user.getPhone())
                .photo(user.getPhoto())
                .email(user.getEmail())
                .addresses(userAddress)
                .build();
    }

    public void editEmail(TokenUserInfo userInfo, UserEmailDto reqDto) {
        // 사용자 찾기
        User user = userRepository.findByAuthId(userInfo.getId()).orElseThrow(
                () -> new CustomException("해당하는 사용자가 없습니다.", HttpStatus.NOT_FOUND)
        );

        // 이메일 넣기
        user.setEmail(reqDto.getEmail());

        // 저장
        userRepository.save(user);
    }

    public void editPhone(TokenUserInfo userInfo, UserPhoneDto userPhoneDto) {
        // 로그인 한 사용자 확인
        User user = userRepository.findByAuthId(userInfo.getId()).orElseThrow(
                () -> new CustomException("해당하는 사용자가 없습니다.", HttpStatus.NOT_FOUND)
        );

        // 인증번호 확인
        String verifyCode = redisTemplate.opsForValue().get("auth:verify:" + userPhoneDto.getPhone());
        if(verifyCode == null || !verifyCode.equals(userPhoneDto.getUserInputCode())) {
            throw new CustomException("인증번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        // 재설정
        user.setPhone(userPhoneDto.getPhone());
        userRepository.save(user);

    }


    // 유저 검증용 true false
    public boolean verifyUser(Long authId, Long userId) {
        return userRepository.findById(userId)
                .map(user -> user.getAuthId().equals(authId))
                .orElse(false);
    }


    public void addBookmark(TokenUserInfo userInfo, UserBookmarkReqDto userBookmarkReqDto) {
        // 로그인 한 사용자 확인
        User user = userCheck(userInfo.getId(), userInfo.getUserId());

        //cookId가 존재하는지 확인
        boolean cookExists = cookServiceClient.checkCook(userBookmarkReqDto.getCookId());

        if (!cookExists) {
            throw new CustomException("해당하는 요리가 없습니다.", HttpStatus.BAD_REQUEST);
        }

        // 즐겨찾기에 없는지 확인
        UserBookmark bookmark = userBookmarkRepository.findByUserIdAndCookId(
                userInfo.getUserId(),
                userBookmarkReqDto.getCookId()
        );

        if (bookmark != null) {
            throw new CustomException("이미 즐겨찾기에 등록되어있습니다.", HttpStatus.BAD_REQUEST);
        }

        // 북마크 추가
        UserBookmark build = UserBookmark.builder().userId(user.getId())
                .cookId(userBookmarkReqDto.getCookId())
                .build();

        userBookmarkRepository.save(build);

        log.info("해당 요리가 즐겨찾기에 등록되었습니다.");
    }

    public void deleteBookmark(TokenUserInfo userInfo, UserBookmarkReqDto userBookmarkReqDto) {
        // 로그인 한 사용자 확인
        User user = userCheck(userInfo.getId(), userInfo.getUserId());

        // 즐겨찾기에 있는지 확인
        UserBookmark bookmark = userBookmarkRepository.findByUserIdAndCookId(
                userInfo.getUserId(),
                userBookmarkReqDto.getCookId()
        );

        if (bookmark == null) {
            throw new CustomException("즐겨찾기에 등록되어있지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        // DB에서 삭제
        userBookmarkRepository.delete(bookmark);
    }

    public List<UserBookmarkResDto> getBookmark(Long id) {
        User user = userRepository.findByAuthId(id).orElseThrow(
                () -> new CustomException("해당하는 사용자가 존재하지 않습니다.", HttpStatus.NOT_FOUND)
        );

        // getCookId()로 cook 페인요청 보내서 썸네일, 요리이름 가져오기
        List<Long> cookIdList = userBookmarkRepository.findByUserId((user.getId()))
                .stream()
                .map(UserBookmark::getCookId)
                .collect(Collectors.toList());

        ResponseEntity<List<UserBookmarkResDto>> cookInfoList = cookServiceClient.cookPreLookup(cookIdList);

        log.info("cookInfoList: {}", cookInfoList);

        if (Objects.requireNonNull(cookInfoList.getBody()).isEmpty() || cookInfoList.getBody() == null) {
            throw new CustomException("조회된 cook 정보가 없습니다.", HttpStatus.NO_CONTENT);
        }

        // cookId를 기준으로 매핑
        Map<Long, UserBookmarkResDto> cookInfoMap = cookInfoList.getBody().stream()
                .collect(Collectors.toMap(UserBookmarkResDto::getCookId, Function.identity()));

        return cookIdList.stream()
                .map(cookId -> {
                    UserBookmarkResDto cook = cookInfoMap.get(cookId);
                    if (cook == null) {
                        log.warn("cookId={}에 대한 정보가 없음. 생략합니다.", cookId);
                        return null;
                    }
                    return cook;
                })
                .filter(Objects::nonNull)
                .toList();
    }

    public PreOrderUserResDto getPreOrderUser(TokenUserInfo userInfo) {
        log.info("id: {}, userId: {}", userInfo.getId(), userInfo.getUserId());

        Long id = userInfo.getId();

        User user = userCheck(id, userInfo.getUserId());

        //모든 주소 정보, 사용자 주소 id, 전화번호 조회
        List<UserAddressDto> userAddress =
                userAddressRepository.findByActiveUserId(userInfo.getUserId())
                        .stream()
                        .map(address -> new UserAddressDto(address.getId(), address.getRoadFull(), address.getAddrDetail()))
                        .toList();

        if (userAddress.isEmpty()) {
            userAddress = null;
        }

        String email = user.getEmail();
        if (email == null) {
            email = "example@gmail.com"; // 결제할 때 필요한 메일 정보 생성
        }

        return PreOrderUserResDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .phone(user.getPhone())
                .addresses(userAddress)
                .email(email)
                .build();
    }

    @Transactional
    public String editPhoto(TokenUserInfo userInfo, MultipartFile image, String category) {
        log.info("사용자 이미지 수정 시작 | userId : {}", userInfo.getUserId());

        User user = userRepository.findById(userInfo.getUserId()).orElseThrow(
                () -> new CustomException("해당하는 사용자가 없습니다.", HttpStatus.NOT_FOUND)
        );

        String url = null;
        try {
            // url 생성
            url = apiServiceClient.changeImage(image, category, user.getPhoto()).getBody();
            user.setPhoto(url);
        } catch (Exception e) {
            log.error("사진을 등록하는 URL을 발급받는 과정에서 오류가 발생했습니다.", e);
            throw new CustomException("사진을 등록하는 URL을 발급받는 과정에서 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return url;
    }

    // 로그인 한 사용자 확인
    public User userCheck(Long authId, Long userId) {

        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException("해당하는 사용자가 없습니다.", HttpStatus.NOT_FOUND)
        );

        if (!user.getAuthId().equals(authId)) {
            throw new CustomException("잘못된 사용자 정보", HttpStatus.BAD_REQUEST);
        }

        return user;
    }

    public Long getUserId(Long id) {
        User userByAuthId = userRepository.getUserByAuthId(id).orElseThrow(
                () -> new EntityNotFoundException("유저가 없습니다.")
        );

        return userByAuthId.getId();
    }

    public void kakaoUserSignUp(UserSignupReqDto reqDto) {
        log.info("사용자 회원가입 시작 : {}", reqDto);

        User user = User.builder()
                .authId(reqDto.getAuthId())
                .nickname(reqDto.getNickname())
                .phone(reqDto.getPhone())
                .email(reqDto.getEmail())
                .photo(reqDto.getPhoto())
                .build();

        // user DB에 저장
        userRepository.save(user);

        // 사용자 주소 저장
        if (reqDto.getRoadFull() != null) {
            UserAddressReqDto addrDto = UserAddressReqDto.builder()
                    .roadFull(reqDto.getRoadFull())
                    .addrDetail(reqDto.getAddrDetail())
                    .build();

            userAddressService.addAddress(reqDto.getAuthId(), addrDto);
            log.info("성공적으로 사용자의 주소를 저장했습니다.");
        }

        log.info("성공적으로 사용자 회원가입이 완료되었습니다.");

    }

    /**
     * 관리자 전용 전체 주문 조회용(사용자 정보)
     */
    public UserInfoResDto getUserInfoByAddressId(Long userAddressId) {
        UserAddress address = userAddressRepository.findById(userAddressId)
                .orElseThrow(() -> new CustomException("주소를 찾을 수 없습니다", HttpStatus.NOT_FOUND));

        User user = userRepository.findById(address.getUserId())
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다", HttpStatus.NOT_FOUND));

        return UserInfoResDto.builder()
                .authId(user.getAuthId())
                .nickname(user.getNickname())
                .roadFull(address.getRoadFull())
                .address(address.getAddrDetail())
                .phone(user.getPhone())
                .build();
    }

    /**
     * userId로 authId를 조회함
     */
    public Long getAuthIdByUserId(Long userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    log.info("userId = {}, authId = {}", user.getId(), user.getAuthId());
                    return user.getAuthId();
                })
                .orElseThrow(() -> new CustomException("해당 유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
    }

    /**
     * 해당 주소 ID를 가진 user_address 조회 후, 그 주소에 연결된 유저의 ID (userId)를 반환
     */
    public Long getUserLoginId(Long userAddressId) {
        return userAddressRepository.findById(userAddressId)
                .map(UserAddress::getUserId)
                .orElseThrow(() -> new CustomException("해당 주소에 연결된 유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
    }

    /**
     * nickname 중복 체크
     */
    public void checkNickname(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new CustomException("이미 사용 중인 닉네임입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * email 중복 체크
     */
    public void checkEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new CustomException("이미 사용 중인 이메일입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 게시글 등록용(유저 정보 반환)
     */
    public Long getUserIdByAddress(Long userAddressId) {
        return userAddressRepository.findById(userAddressId)
                .orElseThrow(() -> new CustomException("주소가 존재하지 않습니다.", HttpStatus.NOT_FOUND))
                .getUserId();
    }


    public boolean getBookmarkById(Long userId, Long cookId) {
        return userBookmarkRepository.existsByUserIdAndCookId(userId, cookId);
    }

    public UserPostInfoResDto getPostUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("사용자가 존재하지 않습니다.", HttpStatus.NOT_FOUND));

        return UserPostInfoResDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .authId(user.getAuthId())
                .build();
    }

    /**
     * 사용자 게시글 조회용 (닉네임)
     */
    public String getNicknameById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("해당 유저가 존재하지 않습니다.", HttpStatus.NOT_FOUND));
        return user.getNickname();
    }

    /**
     * 게시글 좋아요 등록
     */
    @Transactional
    public UserLikeResDto likePost(TokenUserInfo userInfo, Long postId) {
        Long userId = userInfo.getUserId();

        boolean exists = postServiceClient.checkPostExists(postId);
        if (!exists) {
            throw new CustomException("게시글이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
        }

        if (userLikeRepository.findByUserIdAndPostId(userId, postId).isPresent()) {
            throw new CustomException("이미 좋아요한 게시글입니다.", HttpStatus.BAD_REQUEST);
        }

        userLikeRepository.save(
                UserLike.builder()
                        .userId(userId)
                        .postId(postId)
                        .build()
        );
        return UserLikeResDto.builder()
                .postId(postId)
                .build();
    }

    /**
     * 게시글 좋아요 해제
     */
    @Transactional
    public UserLikeResDto unlikePost(TokenUserInfo userInfo, Long postId) {
        Long userId = userInfo.getUserId();

        boolean exists = postServiceClient.checkPostExists(postId);
        if (!exists) {
            throw new CustomException("게시글이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
        }

        UserLike like = userLikeRepository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(() -> new CustomException("좋아요 정보가 없습니다.", HttpStatus.NOT_FOUND));

        userLikeRepository.delete(like);

        return UserLikeResDto.builder()
                .postId(postId)
                .build();
    }

    /**
     * 게시글의 좋아요 개수를 조회
     */
    public Long getLikeCount(Long postId) {
        long count = userLikeRepository.countByPostId(postId);
        return Math.max(count - 1, 0);
    }

    /**
     * 사용자가 좋아요 누른 게시글 목록을 페이징하여 조회
     */
    public LikedPostPagedResDto getLikedPosts(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserLike> pageResult = userLikeRepository.findAllByUserId(userId, pageable);

        List<Long> postIds = pageResult.getContent().stream()
                .map(UserLike::getPostId)
                .toList();

        return new LikedPostPagedResDto(
                postIds,
                page,
                size,
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.isLast()
        );
    }

    /**
     * 모든 게시글에 대한 좋아요 수
     */
    public Map<Long, Long> getAllLikeCounts() {
        List<PostLikeCount> counts = userLikeRepository.countLikesGroupedByPostId();
        return counts.stream()
                .collect(Collectors.toMap(
                        PostLikeCount::getPostId,
                        c -> Math.max(0L, c.getCount() - 1) // 최소 0 보장
                ));
    }

    /**
     * 좋아요 수 기준으로 게시글을 정렬하여 페이징된 postId 목록을 반환
     */
    public LikedPostPagedResDto getMostLikedPostIds(int page, int size) {
        int offset = page * size;

        List<PostLikeCount> topLiked = userLikeRepository.findMostLikedPosts(offset, size);
        Long totalElements = userLikeRepository.countDistinctPostId();

        List<Long> postIds = topLiked.stream()
                .map(PostLikeCount::getPostId)
                .toList();

        return LikedPostPagedResDto.builder()
                .content(postIds)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages((int) Math.ceil((double) totalElements / size))
                .last((page + 1) * size >= totalElements)
                .build();
    }

    /**
     * 사용자 Id 리스트를 받아 사용자 정보를 조회하여 반환
     */
    public Map<Long, PostUserInfoResDto> getUserInfos(List<Long> userIds) {
        return userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(
                        User::getId,
                        user -> new PostUserInfoResDto(user.getId(), user.getNickname())
                ));
    }

    /**
     * 게시글 ID 목록에 대한 좋아요 수를 group by 조회
     */
    public Map<Long, Long> getLikeCountMap(List<Long> postIds) {
        return userLikeRepository.countLikesByPostIds(postIds).stream()
                .collect(Collectors.toMap(
                        PostLikeCount::getPostId,
                        c -> Math.max(0L, c.getCount() - 1) // 최소 0 보장
                ));
    }

    /**
     * 게시글 디폴트 좋아요 등록
     */
    public void defaultLikePost(Long postId) {
        if (userLikeRepository.existsByPostId(postId)) {
            throw new CustomException("이미 좋아요가 등록된 게시글입니다.", HttpStatus.BAD_REQUEST);
        }
        userLikeRepository.save(
                UserLike.builder()
                        .userId(0L)
                        .postId(postId)
                        .build()
        );
    }

    public boolean checkPostLike(TokenUserInfo userInfo, Long postId) {
        return userLikeRepository.findByUserIdAndPostId(userInfo.getUserId(), postId).isPresent();
    }

    /**
     * 이메일 삭제
     */
    @Transactional
    public void deleteEmail(TokenUserInfo userInfo) {
        // 유저 조회
        User user = userRepository.findById(userInfo.getId())
                .orElseThrow(() -> new CustomException("존재하지 않는 사용자입니다.", HttpStatus.NOT_FOUND));

        // 이메일이 비어있으면 예외
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new CustomException("이미 삭제된 이메일입니다.", HttpStatus.BAD_REQUEST);
        }

        // 이메일 삭제
        user.setEmail(null);

        userRepository.save(user);
    }

}
