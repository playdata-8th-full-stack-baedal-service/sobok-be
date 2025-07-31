package com.sobok.apiservice.api.client;

import com.sobok.apiservice.api.dto.google.GoogleReqDto;
import com.sobok.apiservice.api.dto.google.GoogleResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

// 토큰 발급용 FeignClient
@FeignClient(name = "googleTokenClient", url = "${google.auth-url}")
public interface GoogleTokenFeignClient {
    @PostMapping("/token")
    ResponseEntity<GoogleResDto> getGoogleToken(GoogleReqDto googleTokenRequest);
}


