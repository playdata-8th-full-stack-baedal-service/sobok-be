package com.sobok.authservice.auth.client;


import com.sobok.authservice.auth.dto.response.OauthResDto;
import com.sobok.authservice.common.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "api-service", url = "${API_SERVICE_URL}", configuration = FeignConfig.class)
public interface ApiServiceClient {

    @GetMapping("/api/findByOauthId")
    ResponseEntity<OauthResDto> oauthIdById(@RequestParam("id") Long id);

    @PostMapping("/api/register-image")
    ResponseEntity<String> registerImg(@RequestParam String url);
}
