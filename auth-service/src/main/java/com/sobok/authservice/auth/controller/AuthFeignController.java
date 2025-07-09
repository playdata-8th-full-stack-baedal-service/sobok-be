package com.sobok.authservice.auth.controller;

import com.sobok.authservice.auth.dto.response.AuthRiderInfoResDto;
import com.sobok.authservice.auth.dto.response.AuthRiderResDto;
import com.sobok.authservice.auth.entity.Auth;
import com.sobok.authservice.auth.repository.AuthRepository;
import com.sobok.authservice.auth.service.AuthService;
import com.sobok.authservice.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class AuthFeignController {

    private final AuthService authService;
    private final AuthRepository authRepository;

    /**
     * 라이더 활성화
     */
    @PutMapping("/active-rider")
    public ResponseEntity<Void> activeRider(@RequestParam Long authId) {
        authService.activeRider(authId);
        return ResponseEntity.ok().build();
    }

    /**
     * admin-service 전용 라이더 활성화 상태, 로그인 아이디 가져오는 로직
     */
    @GetMapping("/auth/info")
    public ResponseEntity<AuthRiderInfoResDto> getAuthInfo(@RequestParam Long authId) {
        return ResponseEntity.ok(authService.getRiderAuthInfo(authId));
    }

    /**
     * 주문 조회용 로그인 아이디 전달
     */
    @GetMapping("/auth/login-id")
    public ResponseEntity<String> getLoginId(@RequestParam Long authId) {
        String loginId = authService.getLoginIdByAuthId(authId);
        return ResponseEntity.ok(loginId);
    }
}
