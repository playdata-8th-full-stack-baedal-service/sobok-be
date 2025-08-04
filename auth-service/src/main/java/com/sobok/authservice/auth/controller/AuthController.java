package com.sobok.authservice.auth.controller;


import com.sobok.authservice.auth.dto.info.AuthBaseInfoResDto;
import com.sobok.authservice.auth.dto.request.*;
import com.sobok.authservice.auth.dto.response.*;
import com.sobok.authservice.auth.service.auth.*;
import com.sobok.authservice.common.dto.CommonResponse;
import com.sobok.authservice.common.dto.TokenUserInfo;
import com.sobok.authservice.common.exception.CustomException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final AccountService accountService;
    private final RegisterService registerService;
    private final InfoService infoService;
    private final StatusService statusService;

    /**
     * user 회원가입
     */
    @PostMapping("/user-signup")
    public ResponseEntity<?> createAuth(@Valid @RequestBody AuthUserReqDto authUserReqDto) {

        AuthUserResDto userResDto = registerService.userCreate(authUserReqDto);
        return ResponseEntity.ok().body(CommonResponse.ok(userResDto, "회원가입 성공"));

    }

    /**
     * 아이디 중복 확인
     */
    @GetMapping("/check-id")
    public ResponseEntity<?> checkLoginId(@RequestParam String loginId) {
        registerService.checkLoginId(loginId);
        return ResponseEntity.ok(CommonResponse.ok(null, "사용 가능한 아이디입니다."));
    }

    /**
     * 임시토큰 발급
     */
    @GetMapping("/temp-token")
    public ResponseEntity<?> getTempToken() {
        String tempToken = authService.getTempToken();
        return ResponseEntity.ok().body(CommonResponse.ok(tempToken, "임시 토큰이 발급되었습니다."));
    }

    /**
     * 통합 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthLoginReqDto reqDto) throws EntityNotFoundException, CustomException {
        AuthLoginResDto resDto = authService.login(reqDto);

        // 복구 대상인지에 따라 다른 메세지 전달
        String message = resDto.isRecoveryTarget() ? "계정 복구 대상입니다." : "로그인에 성공하였습니다.";
        return ResponseEntity.ok().body(CommonResponse.ok(resDto, message));
    }

    /**
     * 통합 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader("Authorization") String authorizationHeader, // Authorization 헤더를 받음
            @AuthenticationPrincipal TokenUserInfo userInfo) {

        // 액세스 토큰 추출
        String accessToken = authorizationHeader.replace("Bearer ", "");

        // 액세스 토큰과 사용자 정보를 전달
        authService.logout(userInfo, accessToken);

        return ResponseEntity.ok().body(CommonResponse.ok(userInfo.getId(), "로그아웃에 성공하였습니다."));
    }

    /**
     * 토큰 재발급
     */
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestBody AuthReissueReqDto reqDto) throws EntityNotFoundException, CustomException {
        String accessToken = authService.reissue(reqDto);
        return ResponseEntity.ok().body(CommonResponse.ok(accessToken, "토큰이 성공적으로 발급되었습니다."));
    }

    /**
     * 비밀번호 검증
     */
    @PostMapping("/verify-password")
    public ResponseEntity<?> verifyPassword(@AuthenticationPrincipal TokenUserInfo userInfo,
                                            @RequestBody AuthPasswordReqDto reqDto) {
        authService.verifyPassword(userInfo, reqDto);
        return ResponseEntity.ok(CommonResponse.ok(null, "비밀번호가 확인되었습니다."));
    }

    /**
     * 사용자 비활성화(탈퇴)
     */
    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestHeader("Authorization") String authorizationHeader, @AuthenticationPrincipal TokenUserInfo userInfo) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        statusService.delete(accessToken, userInfo);
        return ResponseEntity.ok(CommonResponse.ok(userInfo.getId(), "사용자가 정상적으로 비활성화되었습니다."));
    }

    /**
     * 사용자 복구
     */
    @PostMapping("/recover/{id}")
    public ResponseEntity<?> recover(@PathVariable Long id) throws EntityNotFoundException, CustomException {
        statusService.recover(id);
        return ResponseEntity.ok().body(CommonResponse.ok(id, "사용자의 계정이 정상적으로 복구되었습니다."));
    }

    /**
     * rider 회원가입
     */
    @PostMapping("/rider-signup")
    public ResponseEntity<?> createRider(@Valid @RequestBody AuthRiderReqDto authRiderReqDto) {
        AuthRiderResDto riderResDto = registerService.riderCreate(authRiderReqDto);
        return ResponseEntity.ok().body(CommonResponse.ok(riderResDto, "라이더 회원가입 성공"));
    }

    /**
     * 가게 등록
     */
    @PostMapping("/shop-signup")
    public ResponseEntity<?> createShop(@Valid @RequestBody AuthShopReqDto authShopReqDto, @AuthenticationPrincipal TokenUserInfo userInfo) {
        AuthShopResDto shopResDto = registerService.shopCreate(authShopReqDto);
        return ResponseEntity.ok().body(CommonResponse.ok(shopResDto, "가게 회원가입 성공"));
    }

    /**
     * 사용자 아이디 찾기
     */
    @PostMapping("/findLoginId")
    public ResponseEntity<?> getFindUserId(@Valid @RequestBody AuthFindIdReqDto authFindReqDto) {  //전화번호, inputNumber
        List<AuthFindIdResDto> authFindIdResDto = accountService.userFindId(authFindReqDto);
        return ResponseEntity.ok().body(CommonResponse.ok(authFindIdResDto, "사용자 아이디 찾기 성공"));
    }

    /**
     * 통합 비밀번호 찾기
     */
    @PostMapping("/verification")
    public ResponseEntity<?> authVerification(@Valid @RequestBody AuthVerifyReqDto authVerifyReqDto) {
        Long authId = accountService.authVerification(authVerifyReqDto);
        return ResponseEntity.ok()
                .body(CommonResponse.ok(authId, "해당 사용자의 정보 존재 확인 후 인증번호 발송 완료"));
    }

    //2단계
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody AuthResetPwReqDto authResetPwReqDto) {
        accountService.resetPassword(authResetPwReqDto);
        return ResponseEntity.ok().body(CommonResponse.ok(authResetPwReqDto.getAuthId(), "사용자의 비밀번호가 변경되었습니다."));
    }

    /**
     * 통합 비밀번호 변경
     */
    @PatchMapping("/edit-password")
    public ResponseEntity<?> editPassword(@AuthenticationPrincipal TokenUserInfo userInfo, @Valid @RequestBody AuthEditPwReqDto authEditPwReqDto) {
        AuthResetPwReqDto authResetPwReqDto = AuthResetPwReqDto.builder().authId(userInfo.getId())
                .newPassword(authEditPwReqDto.getNewPassword())
                .build();
        accountService.resetPassword(authResetPwReqDto);
        return ResponseEntity.ok().body(CommonResponse.ok(authResetPwReqDto.getAuthId(), "사용자의 비밀번호가 변경되었습니다."));
    }

    /**
     * 회원 정보 조회
     */
    @GetMapping("/get-info")
    public ResponseEntity<?> getInfo(@AuthenticationPrincipal TokenUserInfo userInfo) {
        // 1. 유저 정보 가져오기
        AuthBaseInfoResDto info = infoService.getInfo(userInfo);

        // 2. 리턴
        return ResponseEntity.ok().body(CommonResponse.ok(info, "성공적으로 정보가 조회되었습니다."));
    }

    /**
     * 소셜 user 회원가입
     */
    @PostMapping("/social-user-signup")
    public ResponseEntity<?> createSocialAuth(@Valid @RequestBody AuthByOauthReqDto authByOauthReqDto) {
        log.info("authByOauthReqDto: {}", authByOauthReqDto);
        registerService.socialUserCreate(authByOauthReqDto);
        return ResponseEntity.ok().body(CommonResponse.ok("회원가입 성공"));

    }

    /**
     * rider 회원가입 승인 요청
     */
    @PutMapping("/rider-active")
    public ResponseEntity<CommonResponse<Void>> activeRider(@RequestParam Long riderId) {
        statusService.activeRider(riderId);
        return ResponseEntity.ok(CommonResponse.ok(null, "라이더 계정이 활성화되었습니다."));
    }
}
