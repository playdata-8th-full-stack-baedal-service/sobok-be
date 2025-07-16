package com.sobok.apiservice.api.client;

import com.sobok.apiservice.api.dto.google.GoogleDetailResDto;
import com.sobok.apiservice.api.dto.google.GoogleReqDto;
import com.sobok.apiservice.api.dto.google.GoogleResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

// 토큰 발급용 FeignClient
@FeignClient(name = "googleTokenClient", url = "${google.auth-url}")
public interface GoogleTokenFeignClient {

    @PostMapping("/token")
    GoogleResDto getGoogleToken(GoogleReqDto googleTokenRequest);

    @GetMapping("/tokeninfo")
    GoogleDetailResDto getGoogleDetailInfo(@RequestParam("id_token") String idToken);
}


