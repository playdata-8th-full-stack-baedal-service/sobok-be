package com.sobok.apiservice.api.client;

import com.sobok.apiservice.api.dto.google.GoogleDetailResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// 사용자 정보 조회용 FeignClient
@FeignClient(name = "google", url = "${google.user-url}")
public interface GoogleFeignClient {
    @GetMapping("/userinfo")
    ResponseEntity<GoogleDetailResDto> getUserInfo(@RequestHeader("Authorization") String bearerToken);
}


