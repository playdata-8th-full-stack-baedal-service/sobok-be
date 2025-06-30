package com.sobok.authservice.auth.controller;


import com.sobok.authservice.auth.dto.request.AuthLoginReqDto;
import com.sobok.authservice.auth.dto.request.AuthReissueReqDto;
import com.sobok.authservice.auth.dto.request.AuthRiderReqDto;
import com.sobok.authservice.auth.dto.request.AuthShopReqDto;
import com.sobok.authservice.auth.dto.response.AuthLoginResDto;
import com.sobok.authservice.auth.dto.response.AuthRiderResDto;
import com.sobok.authservice.auth.dto.response.AuthShopResDto;
import com.sobok.authservice.auth.service.AuthService;
import com.sobok.authservice.common.dto.ApiResponse;
import com.sobok.authservice.auth.dto.request.AuthReqDto;
import com.sobok.authservice.auth.dto.response.AuthResDto;
import com.sobok.authservice.auth.entity.Auth;
import com.sobok.authservice.common.dto.TokenUserInfo;
import com.sobok.authservice.common.exception.CustomException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping("/user-signup")
    public ResponseEntity<?> createAuth(@RequestBody AuthReqDto authReqDto) {
        AuthResDto userResDto = authService.userCreate(authReqDto);
        return ResponseEntity.ok().body(ApiResponse.ok(userResDto, "회원가입 성공"));

    }

    /**
     * 통합 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthLoginReqDto reqDto) throws EntityNotFoundException, CustomException {
        AuthLoginResDto resDto = authService.login(reqDto);

        // 복구 대상인지에 따라 다른 메세지 전달
        String message = resDto.isRecoveryTarget() ? "계정 복구 대상입니다." : "로그인에 성공하였습니다.";
        return ResponseEntity.ok().body(ApiResponse.ok(resDto, message));
    }

    /**
     * 통합 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal TokenUserInfo userInfo) {
        authService.logout(userInfo);
        return ResponseEntity.ok().body(ApiResponse.ok(userInfo.getId(),"로그아웃에 성공하였습니다."));
    }

    /**
     * 토큰 재발급
     */
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestBody AuthReissueReqDto reqDto) throws EntityNotFoundException, CustomException {
        String accessToken = authService.reissue(reqDto);
        return ResponseEntity.ok().body(ApiResponse.ok(accessToken, "토큰이 성공적으로 발급되었습니다."));
    }

    /**
     * 사용자 비활성화
     */
    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@AuthenticationPrincipal TokenUserInfo userInfo) throws EntityNotFoundException {
        authService.delete(userInfo);
        return ResponseEntity.ok().body(ApiResponse.ok(userInfo.getId(), "사용자가 정상적으로 비활성화되었습니다."));
    }


    /**
     * 사용자 복구
     */
    @PostMapping("/recover/{id}")
    public ResponseEntity<?> recover(@PathVariable Long id) throws EntityNotFoundException, CustomException {
        authService.recover(id);
        return ResponseEntity.ok().body(ApiResponse.ok(id, "사용자의 계정이 정상적으로 복구되었습니다."));
    }

    @PostMapping("/rider-signup")
    public ResponseEntity<?> createRider(@RequestBody AuthRiderReqDto authRiderReqDto) {
        AuthRiderResDto riderResDto = authService.riderCreate(authRiderReqDto);
        return ResponseEntity.ok().body(ApiResponse.ok(riderResDto, "라이더 회원가입 성공"));
    }

    @PostMapping("/shop-signup")
    public ResponseEntity<?> createShop(@RequestBody AuthShopReqDto authShopReqDto) {
        AuthShopResDto shopResDto = authService.shopCreate(authShopReqDto);
        return ResponseEntity.ok().body(ApiResponse.ok(shopResDto, "가게 회원가입 성공"));
    }
}
