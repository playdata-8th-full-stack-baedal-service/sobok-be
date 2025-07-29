package com.sobok.paymentservice.payment.client;

import com.sobok.paymentservice.common.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "auth-service", configuration = FeignConfig.class)
public interface AuthFeignClient {
    /**
     * 유저 정보 조회용
     */
    @GetMapping("/api/auth/login-id")
    ResponseEntity<String> getLoginId(@RequestParam Long authId);
}
