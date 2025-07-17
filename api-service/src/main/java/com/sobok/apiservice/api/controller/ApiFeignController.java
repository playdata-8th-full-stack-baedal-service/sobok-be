package com.sobok.apiservice.api.controller;

import com.sobok.apiservice.api.dto.kakao.OauthResDto;
import com.sobok.apiservice.api.service.s3.S3Service;
import com.sobok.apiservice.api.service.socialLogin.SocialLoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class ApiFeignController {

    private final S3Service s3Service;
    private final SocialLoginService socialLoginService;

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
     * @param oauthId
     * @return
     */
    @GetMapping("/findByOauthId")
    public OauthResDto findByOauthId(@RequestParam("id") Long oauthId) {
        return socialLoginService.findOauth(oauthId);
    }

}