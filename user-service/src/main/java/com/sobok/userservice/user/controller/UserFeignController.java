package com.sobok.userservice.user.controller;

import com.sobok.userservice.common.dto.ApiResponse;
import com.sobok.userservice.user.dto.info.AuthUserInfoResDto;
import com.sobok.userservice.user.dto.info.UserAddressDto;
import com.sobok.userservice.user.dto.request.UserSignupReqDto;
import com.sobok.userservice.user.dto.response.*;
import com.sobok.userservice.user.repository.UserAddressRepository;
import com.sobok.userservice.user.repository.UserRepository;
import com.sobok.userservice.user.service.UserAddressService;
import com.sobok.userservice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class UserFeignController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final UserAddressService userAddressService;
    private final UserAddressRepository addressRepository;


    @PostMapping("/signup")
    public ResponseEntity<Object> userSignup(@RequestBody UserSignupReqDto reqDto) {
        userService.signup(reqDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/findByPhoneNumber")
    public ResponseEntity<?> getUser(@RequestBody String phoneNumber) {
        UserResDto byPhoneNumber = userService.findByPhoneNumber(phoneNumber);
        log.info("검색한 사용자 정보 with phone number: {}", byPhoneNumber);
        return ResponseEntity.ok().body(byPhoneNumber);

    }

    /**
     * 닉네임 중복 검증
     */
    @GetMapping("/check-nickname")
    public ResponseEntity<Boolean> checkNickname(@RequestParam String nickname) {
        return ResponseEntity.ok(userRepository.existsByNickname(nickname));
    }

    /**
     * 이메일 중복 검증
     */
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        return ResponseEntity.ok(userRepository.existsByEmail(email));
    }

    @GetMapping("/user-info")
    public ResponseEntity<AuthUserInfoResDto> getUserInfo(@RequestParam Long authId) {
        AuthUserInfoResDto resDto = userService.getUserInfo(authId);
        return ResponseEntity.ok().body(resDto);

    }

    /**
     * 장바구니 조회용 사용자 검증
     */
    @GetMapping("/verify-user")
    public ResponseEntity<Boolean> verifyUser(@RequestParam Long authId,
                                              @RequestParam Long userId) {
        boolean matched = userService.verifyUser(authId, userId);
        return ResponseEntity.ok(matched);
    }

    @GetMapping("/get-user-id")
    Long getUserId(@RequestParam Long id) {
        return userService.getUserId(id);
    }

    /**
     * 관리자 전용 전체 주문 조회용(사용자 정보)
     */
    @GetMapping("/admin/user-info")
    public ResponseEntity<UserInfoResDto> getUserInfoByAddressId(@RequestParam Long userAddressId) {
        return ResponseEntity.ok().body(userService.getUserInfoByAddressId(userAddressId));
    }

    /**
     * userAddressId로 userId를 찾음
     */
    @GetMapping("/user-id")
    public ResponseEntity<Long> getUserIdByUserAddressId(@RequestParam Long userAddressId) {
        return ResponseEntity.ok().body(userService.getUserLoginId(userAddressId));
    }

    /**
     * userId를 기반으로 authId 반환
     */
    @GetMapping("/auth-id")
    public ResponseEntity<Long> getAuthIdByUserId(@RequestParam Long userId) {
        return ResponseEntity.ok().body(userService.getAuthIdByUserId(userId));
    }

    @GetMapping("/get-user-address")
    UserLocationResDto getUserAddress(@RequestParam Long userAddressId) {
        return userAddressService.getUserAddress(userAddressId);
    }

    /**
     * 카카오용 user 생성
     */
    @PostMapping("/kakaoUserSignup")
    public ResponseEntity<Object> kakaoUserSignup(@RequestBody UserSignupReqDto reqDto) {
        log.info("reqDto: {}", reqDto);
        userService.kakaoUserSignUp(reqDto);
        return ResponseEntity.ok().build();
    }

    /**
     * userAddressId로 주소 정보를 찾음
     */
    @GetMapping("/findUserAddress")
    public ResponseEntity<List<UserAddressDto>> getRiderAuthInfo(@RequestParam List<Long> id) {
        return ResponseEntity.ok().body(userAddressService.getAddressList(id));
    }

    /**
     * 게시글 조회용(유저 정보)
     */
    @GetMapping("/post-info")
    public ResponseEntity<UserPostInfoResDto> getPostInfo(@RequestParam Long userId) {
        UserPostInfoResDto res = userService.getPostUserInfo(userId);
        return ResponseEntity.ok(res);
    }

    /**
     * 사용자 게시글 조회용 (닉네임)
     */
    @GetMapping("/user/nickname")
    public ResponseEntity<String> getNicknameById(@RequestParam Long userId) {
        String nickname = userService.getNicknameById(userId);
        return ResponseEntity.ok(nickname);
    }

    /**
     * 게시글의 좋아요 개수를 조회
     */
    @GetMapping("/user-like/count/{postId}")
    public Long getLikeCount(@PathVariable Long postId) {
        return userService.getLikeCount(postId);
    }

    /**
     * 사용자가 좋아요 누른 게시글 목록을 페이징하여 조회
     */
    @GetMapping("/user-like/liked-posts")
    public LikedPostPagedResDto getLikedPostIds(
            @RequestParam Long userId,
            @RequestParam int page,
            @RequestParam int size
    ) {
        return userService.getLikedPosts(userId, page, size);
    }

    /**
     * 전체 게시글의 좋아요 수 Map 반환
     */
    @GetMapping("/user-like/all-counts")
    public Map<Long, Long> getAllLikeCounts() {
        return userService.getAllLikeCounts();
    }

    /**
     * 좋아요 수 기준 인기 게시글 목록을 페이징하여 반환
     */
    @GetMapping("/user-like/most-liked")
    public LikedPostPagedResDto getMostLikedPostIds(
            @RequestParam int page,
            @RequestParam int size
    ) {
        return userService.getMostLikedPostIds(page, size);
    }

    /**
     * 사용자 Id 리스트를 받아 사용자 정보를 조회하여 반환
     */
    @PostMapping("/post-info/batch")
    public Map<Long, PostUserInfoResDto> getUserInfos(@RequestBody List<Long> userIds) {
        return userService.getUserInfos(userIds);
    }

    /**
     * 게시글 ID 목록의 좋아요 수를 Map 형태로 반환
     */
    @PostMapping("/user-like/count-map")
    public Map<Long, Long> getLikeCountMap(@RequestBody List<Long> postIds) {
        return userService.getLikeCountMap(postIds);
    }

    /**
     * 게시글 기본 좋아요 등록
     */
    @PostMapping("/post-like")
    public ResponseEntity<?> likePost(@RequestParam Long postId) {
        userService.defaultLikePost(postId);
        return ResponseEntity.ok("좋아요 등록 완료");
    }

}
