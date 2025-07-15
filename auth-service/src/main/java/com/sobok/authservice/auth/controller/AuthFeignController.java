package com.sobok.authservice.auth.controller;

import com.sobok.authservice.auth.dto.request.AuthSignupReqDto;
import com.sobok.authservice.auth.dto.response.AuthLoginResDto;
import com.sobok.authservice.auth.dto.response.AuthRiderInfoResDto;
import com.sobok.authservice.auth.dto.response.AuthUserResDto;
import com.sobok.authservice.auth.dto.response.OauthResDto;
import com.sobok.authservice.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class AuthFeignController {

    private final AuthService authService;

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

    //oauthId가 존재할 때 - oauthId로 authId 찾기
    @GetMapping("/findByOauthId")
    OauthResDto authIdById(@RequestParam("id") Long id) {
        OauthResDto byOauthId = authService.findByOauthId(id);
        log.info("authId: {}", byOauthId);
        return byOauthId;
    }

    @GetMapping("/kakao-token")
    AuthLoginResDto kakaoToken(@RequestParam("authId") Long id) {
        log.info("여기는 토큰생성하는길");
        return authService.kakaoLoginToken(id);
    }

    /**
     * 주문 조회용 로그인 아이디 전달
     */
    @GetMapping("/auth/login-id")
    public ResponseEntity<String> getLoginId(@RequestParam Long authId) {
        String loginId = authService.getLoginIdByAuthId(authId);
        return ResponseEntity.ok(loginId);
    }

    @GetMapping("/get-rider-inactive")
    List<Long> getInactiveRidersInfo() {
        return authService.getInactiveRidersInfo();
    }
}
