package com.sobok.authservice.auth.controller;

import com.sobok.authservice.auth.dto.request.AuthSignupReqDto;
import com.sobok.authservice.auth.dto.response.AuthLoginResDto;
import com.sobok.authservice.auth.dto.response.AuthUserResDto;
import com.sobok.authservice.auth.dto.response.OauthResDto;
import com.sobok.authservice.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
     * 카카오 회원가입 - auth 생성
     */
    @PostMapping("/kakao-auth")
    public ResponseEntity<?> kakaoCallback(@RequestBody AuthSignupReqDto authSignupReqDto) {
        log.info("kakao-auth request: {}", authSignupReqDto);
        // 회원가입
        OauthResDto oauthResDto = authService.authSignup(authSignupReqDto);//authId와 닉네임
        return ResponseEntity.ok().body(oauthResDto);
    }

    //oauthId가 존재할 때 - oauthId로 authId 찾기
    @GetMapping("/findByOauthId")
    OauthResDto authIdById(@RequestParam("id") Long id){
        OauthResDto byOauthId = authService.findByOauthId(id);
        log.info("authId: {}", byOauthId);
        return byOauthId;
    }

    @GetMapping("/kakao-token")
    AuthLoginResDto kakaoToken(@RequestParam("authId") Long id) {
        log.info("여기는 토큰생성하는길");
        return authService.kakaoLoginToken(id);
    }
}
