package com.sobok.deliveryservice.delivery.client;

import com.sobok.deliveryservice.common.config.FeignConfig;
import com.sobok.deliveryservice.delivery.dto.response.RiderInfoResDto;
import com.sobok.deliveryservice.delivery.dto.response.RiderResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "auth-service", url = "${AUTH_SERVICE_URL}", configuration = FeignConfig.class)
public interface AuthFeignClient {

    /**
     * 라이더 정보
     */
    @GetMapping("/api/auth/info")
    ResponseEntity<RiderInfoResDto> getRiderAuthInfo(@RequestParam Long authId);

    @GetMapping("/api/get-rider-inactive")
    List<Long> getInactiveRidersInfo();
}
