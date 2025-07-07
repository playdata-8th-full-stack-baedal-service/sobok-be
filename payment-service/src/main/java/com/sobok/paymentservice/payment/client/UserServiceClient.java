package com.sobok.paymentservice.payment.client;

import com.sobok.paymentservice.common.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", configuration = FeignConfig.class)
public interface UserServiceClient {

    // 유저 검증
    @GetMapping("/api/verify-user")
    Boolean verifyUser(@RequestParam Long authId,
                       @RequestParam Long userId);
}