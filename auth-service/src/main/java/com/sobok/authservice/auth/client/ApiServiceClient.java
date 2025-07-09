package com.sobok.authservice.auth.client;


import com.sobok.authservice.auth.dto.response.OauthResDto;
import com.sobok.authservice.common.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "api-service", configuration = FeignConfig.class)
public interface ApiServiceClient {

    @GetMapping("/api/findByOauthId")
    OauthResDto oauthIdById(@RequestParam("id") Long id);
}
