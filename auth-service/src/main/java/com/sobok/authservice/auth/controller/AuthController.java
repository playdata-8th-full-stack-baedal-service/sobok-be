package com.sobok.authservice.auth.controller;


import com.sobok.authservice.auth.dto.request.AuthLoginReqDto;
import com.sobok.authservice.auth.dto.request.AuthReissueReqDto;
import com.sobok.authservice.auth.dto.response.AuthLoginResDto;
import com.sobok.authservice.auth.service.AuthService;
import com.sobok.authservice.common.dto.ApiResponse;
import com.sobok.authservice.auth.dto.request.AuthReqDto;
import com.sobok.authservice.auth.dto.response.AuthResDto;
import com.sobok.authservice.auth.entity.Auth;
import com.sobok.authservice.common.dto.TokenUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping("/user-signup")
    public ResponseEntity<?> createAuth(@RequestBody AuthReqDto authReqDto) {
        Auth savedUser = authService.userCreate(authReqDto);

        AuthResDto responseData =
                new AuthResDto(savedUser.getId(), savedUser.getLoginId(), authReqDto.getNickname());

        return new ResponseEntity<>(ApiResponse.ok(responseData, "사용자 회원가입이 완료되었습니다."), HttpStatus.OK);

    }
  
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthLoginReqDto reqDto) throws Exception {
        AuthLoginResDto resDto = authService.login(reqDto);

        // 복구 대상인지에 따라 다른 메세지 전달
        String message = resDto.isRecoveryTarget() ? "계정 복구 대상입니다." : "로그인에 성공하였습니다.";
        return ResponseEntity.ok().body(ApiResponse.ok(resDto, message));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal TokenUserInfo userInfo) {
        authService.logout(userInfo);
        return ResponseEntity.ok().body(ApiResponse.ok(userInfo.getId(),"로그아웃에 성공하였습니다."));
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestBody AuthReissueReqDto reqDto) {
        String accessToken = authService.reissue(reqDto);
        return ResponseEntity.ok().body(ApiResponse.ok(accessToken, "토큰이 성공적으로 발급되었습니다."));
    }

//    @PostMapping("/rider-signup")
//    public ResponseEntity<?> createRider(@RequestBody AuthReqDto authReqDto) {
//        Auth savedUser = authService.riderCreate(authReqDto);
//    }
}
