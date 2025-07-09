package com.sobok.adminservice.admin.client;

import com.sobok.adminservice.common.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "auth-service", configuration = FeignConfig.class)
public interface AdminFeignClient {

    /**
     * 라이더 승인 용
     */
    @PutMapping("/api/active-rider")
    void activeRider(@RequestParam Long authId);

    /**
     * 유저 정보 조회용
     */
    @GetMapping("/api/auth/login-id")
    String getLoginId(@RequestParam Long authId);
}
