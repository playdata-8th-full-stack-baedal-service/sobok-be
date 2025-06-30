package com.sobok.userservice.user.controller;


import com.sobok.userservice.common.dto.ApiResponse;
import com.sobok.userservice.user.dto.response.UserResDto;
import com.sobok.userservice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/findByPhoneNumber")
    public ResponseEntity<?> getUser(@RequestBody String phoneNumber) {

        UserResDto byPhoneNumber = userService.findByPhoneNumber(phoneNumber);

        log.info("검색한 사용자 정보 with phone number: {}", byPhoneNumber);

        return ResponseEntity.ok().body(ApiResponse.ok(byPhoneNumber, "전화번호로 찾은 user 정보입니다."));

    }


}
