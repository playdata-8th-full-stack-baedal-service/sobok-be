package com.sobok.userservice.user.controller;

import com.sobok.userservice.common.dto.ApiResponse;
import com.sobok.userservice.user.dto.info.AuthUserInfoResDto;
import com.sobok.userservice.user.dto.request.UserSignupReqDto;
import com.sobok.userservice.user.dto.response.UserInfoResDto;
import com.sobok.userservice.user.dto.response.UserResDto;
import com.sobok.userservice.user.repository.UserRepository;
import com.sobok.userservice.user.service.UserAddressService;
import com.sobok.userservice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class UserFeignController {
    private final UserService userService;
    private final UserRepository userRepository;


    @PostMapping("/signup")
    public ResponseEntity<Object> userSignup(@RequestBody UserSignupReqDto reqDto) {
        userService.signup(reqDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/findByPhoneNumber")
    public ResponseEntity<?> getUser(@RequestBody String phoneNumber) {
        UserResDto byPhoneNumber = userService.findByPhoneNumber(phoneNumber);
        log.info("검색한 사용자 정보 with phone number: {}", byPhoneNumber);
        return ResponseEntity.ok().body(ApiResponse.ok(byPhoneNumber, "전화번호로 찾은 user 정보입니다."));

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
    public UserInfoResDto getUserInfoByAddressId(@RequestParam Long userAddressId) {
        return userService.getUserInfoByAddressId(userAddressId);
    }

    /**
     * userAddressId로 userId를 찾음
     */
    @GetMapping("/user-id")
    public ResponseEntity<Long> getUserIdByUserAddressId(@RequestParam Long userAddressId) {
        return ResponseEntity.ok(userService.getUserLoginId(userAddressId));
    }

    /**
     * userId를 기반으로 authId 반환
     */
    @GetMapping("/auth-id")
    public ResponseEntity<Long> getAuthIdByUserId(@RequestParam Long userId) {
        return ResponseEntity.ok(userService.getAuthIdByUserId(userId));
    }
}
