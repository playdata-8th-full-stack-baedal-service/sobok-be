package com.sobok.authservice.auth.controller;


import com.sobok.authservice.auth.dto.request.AuthLoginReqDto;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthLoginReqDto reqDto) throws Exception {
        AuthLoginResDto resDto = authService.login(reqDto);
        return ResponseEntity.ok().body(ApiResponse.ok(resDto, "로그인에 성공하였습니다."));
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
