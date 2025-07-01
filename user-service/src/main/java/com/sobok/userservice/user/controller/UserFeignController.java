package com.sobok.userservice.user.controller;

import com.sobok.userservice.common.dto.ApiResponse;
import com.sobok.userservice.user.dto.request.UserSignupReqDto;
import com.sobok.userservice.user.dto.response.UserResDto;
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

}
