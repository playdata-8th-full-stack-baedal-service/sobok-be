package com.sobok.authservice.auth.controller;

import com.sobok.authservice.auth.dto.request.AuthLoginReqDto;
import com.sobok.authservice.auth.dto.response.AuthLoginResDto;
import com.sobok.authservice.auth.service.AuthService;
import com.sobok.authservice.auth.service.UserService;
import com.sobok.authservice.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/user-signup")
    public ResponseEntity<?> createUser() {
        userService.userCreate();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthLoginReqDto reqDto) throws Exception {
        AuthLoginResDto resDto = authService.login(reqDto);
        return ResponseEntity.ok().body(ApiResponse.ok(resDto, "로그인에 성공하였습니다."));
    }

}
