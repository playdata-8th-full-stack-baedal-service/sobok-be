package com.sobok.apiservice.api.controller;

import com.sobok.apiservice.api.controller.docs.SocialLoginControllerDocs;
import com.sobok.apiservice.api.dto.google.GoogleCallResDto;
import com.sobok.apiservice.api.dto.kakao.KakaoCallResDto;
import com.sobok.apiservice.api.service.socialLogin.GoogleLoginService;
import com.sobok.apiservice.api.service.socialLogin.KakaoLoginService;
import com.sobok.apiservice.api.service.socialLogin.SocialLoginService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/social")
@Slf4j
public class SocialLoginController implements SocialLoginControllerDocs {

    private final SocialLoginService socialLoginService;
    private final KakaoLoginService kakaoLoginService;
    private final GoogleLoginService googleLoginService;

    /**
     * 카카오 로그인/회원가입
     */
    // 카카오 콜백 요청 처리
    @GetMapping("/kakao-login")
    public void kakaoCallback(@RequestParam String code, HttpServletResponse response) throws IOException {
        KakaoCallResDto kakaoCallResDto = kakaoLoginService.kakaoCallback(code);
        socialLoginService.writeSocialLoginResponse(kakaoCallResDto, "KAKAO", response);
    }

    /**
     * 구글 로그인/회원가입
     */
    @GetMapping("/google-login")
    public void googleCallback(@RequestParam String code, HttpServletResponse response) throws IOException {
        GoogleCallResDto googleCallResDto = googleLoginService.googleCallback(code);
        socialLoginService.writeSocialLoginResponse(googleCallResDto, "GOOGLE", response);
    }

    @GetMapping("/google-login-view")
    public ResponseEntity<?> getGoogleLoginView() {
        return ResponseEntity.ok(googleLoginService.getGoogleLoginView());
    }
}
