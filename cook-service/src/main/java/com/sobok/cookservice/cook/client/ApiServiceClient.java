package com.sobok.cookservice.cook.client;


import com.sobok.cookservice.common.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "api-service", url = "${API_SERVICE_URL}", configuration = FeignConfig.class)
public interface ApiServiceClient {
    @PostMapping("/api/register-image")
    String registerImg(@RequestParam String url);
}
