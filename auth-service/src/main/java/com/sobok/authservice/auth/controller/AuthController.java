package com.sobok.authservice.auth.controller;


import com.sobok.authservice.auth.dto.request.AuthLoginReqDto;
import com.sobok.authservice.auth.dto.response.AuthLoginResDto;
import com.sobok.authservice.auth.service.AuthService;
import com.sobok.authservice.auth.service.UserService;
import com.sobok.authservice.common.dto.ApiResponse;
import com.sobok.authservice.auth.dto.AuthReqDto;
import com.sobok.authservice.auth.dto.AuthResDto;
import com.sobok.authservice.auth.dto.ResponseDto;
import com.sobok.authservice.auth.entity.Auth;
import com.sobok.authservice.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        Auth savedUser = authService.userCreate(authReqDto);

        AuthResDto responseData =
                new AuthResDto(savedUser.getId(), savedUser.getLoginId(), authReqDto.getNickname());


        ResponseDto<AuthResDto> response = new ResponseDto<>(
                true,
                200,
                "사용자 회원가입이 완료되었습니다.",
                responseData
        );

        return new ResponseEntity<>(response, HttpStatus.OK);

    }
  
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthLoginReqDto reqDto) throws Exception {
        AuthLoginResDto resDto = authService.login(reqDto);
        return ResponseEntity.ok().body(ApiResponse.ok(resDto, "로그인에 성공하였습니다."));
    }

//    @PostMapping("/rider-signup")
//    public ResponseEntity<?> createRider(@RequestBody AuthReqDto authReqDto) {
//        Auth savedUser = authService.riderCreate(authReqDto);
//    }
}
