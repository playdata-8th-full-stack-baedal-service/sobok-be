package com.sobok.authservice.auth.controller;


import com.sobok.authservice.auth.dto.info.AuthBaseInfoResDto;
import com.sobok.authservice.auth.dto.request.*;
import com.sobok.authservice.auth.dto.response.*;
import com.sobok.authservice.auth.service.AuthService;
import com.sobok.authservice.auth.service.info.AuthInfoProvider;
import com.sobok.authservice.auth.service.info.AuthInfoProviderFactory;
import com.sobok.authservice.common.dto.ApiResponse;
import com.sobok.authservice.common.dto.TokenUserInfo;
import com.sobok.authservice.common.enums.Role;
import com.sobok.authservice.common.exception.CustomException;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * user 회원가입
     */
    @PostMapping("/user-signup")
    public ResponseEntity<?> createAuth(@Valid @RequestBody AuthUserReqDto authUserReqDto) {

        AuthUserResDto userResDto = authService.userCreate(authUserReqDto);
        return ResponseEntity.ok().body(ApiResponse.ok(userResDto, "회원가입 성공"));

    }

    /**
     * 아이디 중복 확인
     */
    @GetMapping("/check-id")
    public ResponseEntity<?> checkLoginId(@RequestParam String loginId) {
        authService.checkLoginId(loginId);
        return ResponseEntity.ok(ApiResponse.ok(null, "사용 가능한 아이디입니다."));
    }

    /**
     * user 닉네임 중복 확인
     */
    @GetMapping("/check-nickname")
    public ResponseEntity<?> checkNickname(@RequestParam String nickname) {
        authService.checkNickname(nickname);
        return ResponseEntity.ok(ApiResponse.ok(null, "사용 가능한 닉네임입니다."));
    }

    /**
     * email 중복 확인
     */
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        authService.checkEmail(email);
        return ResponseEntity.ok(ApiResponse.ok(null, "사용 가능한 이메일입니다."));
    }

    /**
     * 라이더 면허 번호 중복 확인
     */
    @GetMapping("/check-permission")
    public ResponseEntity<?> checkPermission(@RequestParam String permission) {
        authService.checkPermission(permission);
        return ResponseEntity.ok(ApiResponse.ok(null, "사용 가능한 면허번호 입니다."));
    }

    /**
     * 가게 이름 중복 확인
     */
    @GetMapping("/check-shopName")
    public ResponseEntity<?> checkShopName(@RequestParam String shopName) {
        authService.checkShopName(shopName);
        return ResponseEntity.ok(ApiResponse.ok(null, "사용 가능한 지점명 입니다."));
    }

    /**
     * 가게 주소 중복 확인
     */
    @GetMapping("/check-shopAddress")
    public ResponseEntity<?> checkShopAddress(@RequestParam String shopAddress) {
        authService.checkShopAddress(shopAddress);
        return ResponseEntity.ok(ApiResponse.ok(null, "사용 가능한 주소 입니다."));
    }


    /**
     * 임시토큰 발급
     */
    @GetMapping("/temp-token")
    public ResponseEntity<?> getTempToken() {
        String tempToken = authService.getTempToken();
        return ResponseEntity.ok().body(ApiResponse.ok(tempToken, "임시 토큰이 발급되었습니다."));
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
        return ResponseEntity.ok().body(ApiResponse.ok(userInfo.getId(), "로그아웃에 성공하였습니다."));
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
    public ResponseEntity<?> delete(@AuthenticationPrincipal TokenUserInfo userInfo, @RequestBody AuthPasswordReqDto reqDto) throws EntityNotFoundException {
        authService.delete(userInfo, reqDto);
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

    /**
     * rider 회원가입
     */
    @PostMapping("/rider-signup")
    public ResponseEntity<?> createRider(@Valid @RequestBody AuthRiderReqDto authRiderReqDto) {
        AuthRiderResDto riderResDto = authService.riderCreate(authRiderReqDto);
        return ResponseEntity.ok().body(ApiResponse.ok(riderResDto, "라이더 회원가입 성공"));
    }

    /**
     * 가게 등록
     */
    @PostMapping("/shop-signup")
    public ResponseEntity<?> createShop(@Valid @RequestBody AuthShopReqDto authShopReqDto, @AuthenticationPrincipal TokenUserInfo userInfo) {
        AuthShopResDto shopResDto = authService.shopCreate(authShopReqDto, userInfo);
        return ResponseEntity.ok().body(ApiResponse.ok(shopResDto, "가게 회원가입 성공"));
    }

    /**
     * 사용자 아이디 찾기
     */
    @GetMapping("/findLoginId")
    public ResponseEntity<?> getFindUserId(@RequestBody AuthFindIdReqDto authFindReqDto) {  //전화번호, inputNumber
        List<AuthFindIdResDto> authFindIdResDto = authService.userFindId(authFindReqDto);
        return ResponseEntity.ok().body(ApiResponse.ok(authFindIdResDto, "사용자 아이디 찾기 성공"));
    }

    /**
     * 통합 비밀번호 찾기
     */
    @PostMapping("/verification")
    public ResponseEntity<?> authVerification(@Valid @RequestBody AuthVerifyReqDto authVerifyReqDto) {
        Long authId = authService.authVerification(authVerifyReqDto);
        return ResponseEntity.ok()
                .body(ApiResponse.ok(authId, "해당 사용자의 정보 존재 확인 후 인증번호 발송 완료"));
    }

    //2단계
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody AuthResetPwReqDto authResetPwReqDto) {
        authService.resetPassword(authResetPwReqDto);
        return ResponseEntity.ok().body(ApiResponse.ok(authResetPwReqDto.getAuthId(), "사용자의 비밀번호가 변경되었습니다."));

    }

    /**
     * 통합 비밀번호 변경
     */
    @PatchMapping("/edit-password")
    public ResponseEntity<?> editPassword(@AuthenticationPrincipal TokenUserInfo userInfo, @Valid @RequestBody AuthEditPwReqDto authEditPwReqDto) {
        AuthResetPwReqDto authResetPwReqDto = AuthResetPwReqDto.builder().authId(userInfo.getId())
                .newPassword(authEditPwReqDto.getNewPassword())
                .build();
        authService.resetPassword(authResetPwReqDto);
        return ResponseEntity.ok().body(ApiResponse.ok(authResetPwReqDto.getAuthId(), "사용자의 비밀번호가 변경되었습니다."));
    }

    /**
     * 회원 정보 조회
     */
    @PostMapping("/get-info")
    public ResponseEntity<?> getInfo(@AuthenticationPrincipal TokenUserInfo userInfo, @RequestBody AuthPasswordReqDto reqDto) {
        // 1. 비밀번호 확인
        String loginId = authService.verifyByPassword(userInfo.getId(), reqDto);

        // 2. 유저 정보 가져오기
        AuthBaseInfoResDto info = authService.getInfo(userInfo, loginId);

        // 3. 리턴
        return ResponseEntity.ok().body(ApiResponse.ok(info, "성공적으로 정보가 조회되었습니다."));
    }
}
