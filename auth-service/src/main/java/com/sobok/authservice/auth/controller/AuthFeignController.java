package com.sobok.authservice.auth.controller;

import com.sobok.authservice.auth.dto.response.AuthLoginResDto;
import com.sobok.authservice.auth.dto.response.AuthRiderInfoResDto;
import com.sobok.authservice.auth.dto.response.OauthResDto;
import com.sobok.authservice.auth.service.auth.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class AuthFeignController {

    private final AuthService authService;
    private final InfoService infoService;
    private final StatusService statusService;


    /**
     * admin-service 전용 라이더 활성화 상태, 로그인 아이디 가져오는 로직
     */
    @GetMapping("/auth/info")
    public ResponseEntity<AuthRiderInfoResDto> getAuthInfo(@RequestParam Long authId) {
        return ResponseEntity.ok(infoService.getRiderAuthInfo(authId));
    }

    //oauthId가 존재할 때 - oauthId로 authId 찾기
    @GetMapping("/findByOauthId")
    OauthResDto authIdById(@RequestParam("id") Long id) {
        OauthResDto byOauthId = infoService.findByOauthId(id);
        log.info("authId: {}", byOauthId);
        return byOauthId;
    }

    @GetMapping("/social-token")
    AuthLoginResDto socialToken(@RequestParam("authId") Long id) {
        return authService.socialLoginToken(id);
    }

    /**
     * 주문 조회용 로그인 아이디 전달
     */
    @GetMapping("/auth/login-id")
    public ResponseEntity<String> getLoginId(@RequestParam Long authId) {
        String loginId = infoService.getLoginIdByAuthId(authId);
        return ResponseEntity.ok().body(loginId);
    }

    @GetMapping("/get-rider-inactive")
    List<Long> getInactiveRidersInfo() {
        return infoService.getInactiveRidersInfo();
    }
}
