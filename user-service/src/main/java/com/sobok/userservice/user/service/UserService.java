package com.sobok.userservice.user.service;

import com.sobok.userservice.common.dto.TokenUserInfo;
import com.sobok.userservice.common.exception.CustomException;
import com.sobok.userservice.user.client.CookServiceClient;
import com.sobok.userservice.user.dto.email.UserEmailDto;
import com.sobok.userservice.user.dto.info.AuthUserInfoResDto;
import com.sobok.userservice.user.dto.info.UserAddressDto;
import com.sobok.userservice.user.dto.request.UserAddressReqDto;
import com.sobok.userservice.user.dto.request.UserBookmarkReqDto;
import com.sobok.userservice.user.dto.request.UserPhoneDto;
import com.sobok.userservice.user.dto.request.UserSignupReqDto;
import com.sobok.userservice.user.dto.response.UserBookmarkResDto;
import com.sobok.userservice.user.entity.UserBookmark;
import com.sobok.userservice.user.repository.UserAddressRepository;
import com.sobok.userservice.user.repository.UserBookmarkRepository;
import com.sobok.userservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sobok.userservice.user.dto.response.UserResDto;
import com.sobok.userservice.user.entity.User;

import java.util.List;
import java.util.Optional;
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

    public UserResDto findByPhoneNumber(String phoneNumber) {
        Optional<User> byPhone = userRepository.findByPhone(phoneNumber);
        if (byPhone.isPresent()) {
            User user = byPhone.get();
            log.info("전화번호로 얻어온 auth의 정보: {}", byPhone.toString());
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
    public void signup(UserSignupReqDto reqDto) {
        log.info("사용자 회원가입 시작 : {}", reqDto.getAuthId());

        // 유저 객체 생성
        User user = User.builder()
                .authId(reqDto.getAuthId())
                .nickname(reqDto.getNickname())
                .phone(reqDto.getPhone())
                .photo(reqDto.getPhoto())
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
                userAddressRepository.getUserAddressByUserId(user.getId())
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

        // 재설정
        user.setPhone(userPhoneDto.getPhone());
        userRepository.save(user);

    }

    public void addBookmark(TokenUserInfo userInfo, UserBookmarkReqDto userBookmarkReqDto) {
        // 로그인 한 사용자 확인
        User user = userRepository.findById(userBookmarkReqDto.getUserId()).orElseThrow(
                () -> new CustomException("해당하는 사용자가 없습니다.", HttpStatus.NOT_FOUND)
        );

        if (!user.getAuthId().equals(userInfo.getId())) {
            throw new CustomException("잘못된 사용자 정보", HttpStatus.BAD_REQUEST);
        }

        //cookId가 존재하는지 확인
        if (!cookServiceClient.checkCook(userBookmarkReqDto.getCookId()).hasBody()) {
            throw new CustomException("해당하는 요리가 없습니다.", HttpStatus.BAD_REQUEST);
        }

        // 즐겨찾기에 없는지 확인
        UserBookmark bookmark = userBookmarkRepository.findByUserIdAndCookId(
                userBookmarkReqDto.getUserId(),
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
        User user = userRepository.findById(userBookmarkReqDto.getUserId()).orElseThrow(
                () -> new CustomException("해당하는 사용자가 없습니다.", HttpStatus.NOT_FOUND)
        );

        if (!user.getAuthId().equals(userInfo.getId())) {
            throw new CustomException("잘못된 사용자 정보", HttpStatus.BAD_REQUEST);
        }

        // 즐겨찾기에 있는지 확인
        UserBookmark bookmark = userBookmarkRepository.findByUserIdAndCookId(
                userBookmarkReqDto.getUserId(),
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

        return userBookmarkRepository.findByUserId((user.getId()))
                .stream()
                .map(bookmark -> new UserBookmarkResDto(bookmark.getCookId()))
                .collect(Collectors.toList());
    }
}
