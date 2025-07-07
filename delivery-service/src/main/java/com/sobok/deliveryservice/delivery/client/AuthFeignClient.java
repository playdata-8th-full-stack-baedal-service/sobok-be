package com.sobok.deliveryservice.delivery.client;

import com.sobok.deliveryservice.common.config.FeignConfig;
import com.sobok.deliveryservice.delivery.dto.response.RiderResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "auth-service", configuration = FeignConfig.class)
public interface AuthFeignClient {

    /**
     * 라이더 정보
     */
    @GetMapping("/api/auth/info")
    RiderResDto getRiderAuthInfo(@RequestParam Long authId);
}
