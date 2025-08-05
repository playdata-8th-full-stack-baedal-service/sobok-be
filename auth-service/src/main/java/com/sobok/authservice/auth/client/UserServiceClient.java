package com.sobok.authservice.auth.client;

import com.sobok.authservice.auth.dto.info.AuthUserInfoResDto;
import com.sobok.authservice.auth.dto.request.UserSignupReqDto;
import com.sobok.authservice.common.config.FeignConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.cloud.openfeign.FeignClient;
import com.sobok.authservice.auth.dto.response.*;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "user-service", configuration = FeignConfig.class)
public interface UserServiceClient {

    @PostMapping("/api/findByPhoneNumber")
    ResponseEntity<ByPhoneResDto> findByPhone(@RequestBody String phoneNumber);

    @PostMapping("/api/signup")
    void userSignup(@RequestBody UserSignupReqDto reqDto);

    // 닉네임 중복 검증
    @GetMapping("/api/check-nickname")
    Boolean checkNickname(@RequestParam String nickname);

    // 이메일 중복 검증
    @GetMapping("/api/check-email")
    Boolean checkEmail(@RequestParam String email);
  
    @GetMapping("/api/user-info")
    ResponseEntity<AuthUserInfoResDto> getUserInfo(@RequestParam Long userId);

    @GetMapping("/api/get-user-id")
    ResponseEntity<Long> getUserId(@RequestParam Long id);

    // 전화번호 중복 검증
    @GetMapping("/api/exists-by-phone")
    Boolean existsByPhone(@RequestParam String phone);
}
