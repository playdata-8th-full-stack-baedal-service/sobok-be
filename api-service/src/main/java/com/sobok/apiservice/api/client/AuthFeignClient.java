package com.sobok.apiservice.api.client;

import com.sobok.apiservice.api.dto.kakao.AuthLoginResDto;
import com.sobok.apiservice.api.dto.kakao.OauthResDto;
import com.sobok.apiservice.common.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "auth-service", url = "${AUTH_SERVICE_URL}", configuration = FeignConfig.class)
public interface AuthFeignClient {

    @GetMapping("/api/findByOauthId")
    ResponseEntity<OauthResDto> authIdById(@RequestParam("id") Long id);  //oauthId로 찾기

    @GetMapping("/api/social-token")
    ResponseEntity<AuthLoginResDto> socialToken(@RequestParam Long id);
}
