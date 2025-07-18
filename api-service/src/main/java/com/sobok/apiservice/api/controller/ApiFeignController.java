package com.sobok.apiservice.api.controller;

import com.sobok.apiservice.api.dto.address.LocationResDto;
import com.sobok.apiservice.api.dto.google.GoogleCallResDto;
import com.sobok.apiservice.api.dto.kakao.KakaoCallResDto;
import com.sobok.apiservice.api.dto.kakao.OauthResDto;
import com.sobok.apiservice.api.dto.toss.TossPayReqDto;
import com.sobok.apiservice.api.dto.toss.TossPayResDto;
import com.sobok.apiservice.api.service.address.ConvertAddressService;
import com.sobok.apiservice.api.service.s3.S3Service;
import com.sobok.apiservice.api.service.socialLogin.GoogleLoginService;
import com.sobok.apiservice.api.service.socialLogin.KakaoLoginService;
import com.sobok.apiservice.api.service.socialLogin.SocialLoginService;
import com.sobok.apiservice.api.service.toss.TossPayService;
import com.sobok.apiservice.common.dto.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class ApiFeignController {

    private final S3Service s3Service;
    private final ConvertAddressService convertAddressService;
    private final TossPayService tossPayService;
    private final SocialLoginService socialLoginService;
    private final KakaoLoginService kakaoLoginService;
    private final GoogleLoginService googleLoginService;

    /**
     * FEIGN
     * S3 이미지 등록 - 업로드 후 실제 정보 저장이 완료되면 실행
     */
    @PostMapping("/register-image")
    public String registerImg(@RequestParam String url) {
        return s3Service.registerImage(url);
    }

    /**
     * FEIGN
     * S3 이미지 변경
     */
    @PostMapping("/change-image")
    public String changeImage(@RequestPart MultipartFile image, @RequestPart String category, @RequestPart String oldPhoto) {
        return s3Service.changeImage(image, category, oldPhoto);
    }

    /**
     * FEIGN
     *
     * @param oauthId
     * @return
     */
    @GetMapping("/findByOauthId")
    public ResponseEntity<?> findByOauthId(@RequestParam("id") Long oauthId) {
        return ResponseEntity.ok().body(socialLoginService.findOauth(oauthId));
    }

    @GetMapping("/convert-addr")
    public LocationResDto convertAddress(@RequestParam String roadFull) {
        return convertAddressService.getLocation(roadFull);
    }

    /**
     * S3 사진 삭제
     */
    @DeleteMapping("/delete-S3-image")
    public ResponseEntity<?> deleteS3Image(@RequestParam String key) {
        s3Service.deleteImage(key);
        return ResponseEntity.ok().body(ApiResponse.ok(key, "S3의 파일이 성공적으로 삭제되었습니다."));
    }

    /**
     * S3 이미지 업로드 - 10분 내 register 필요
     */
    @PutMapping("/upload-image/{category}")
    public ResponseEntity<?> putS3Image(@RequestPart MultipartFile image, @PathVariable String category) {
        String imgUrl = s3Service.uploadImage(image, category);
        return ResponseEntity.ok().body(ApiResponse.ok(imgUrl, "S3에 파일이 정상적으로 업로드되었습니다."));
    }

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

    /**
     * 토스페이 결제
     */
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(@RequestBody TossPayReqDto reqDto) {
        TossPayResDto resDto = tossPayService.confirmPayment(reqDto);
        return ResponseEntity.ok().body(ApiResponse.ok(resDto, "정상 처리되었습니다."));
    }
}