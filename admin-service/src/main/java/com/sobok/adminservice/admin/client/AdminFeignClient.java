package com.sobok.adminservice.admin.client;

import com.sobok.adminservice.common.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "auth-service", configuration = FeignConfig.class)
public interface AdminFeignClient {

    @PutMapping("/api/active-rider")
    void activeRider(@RequestParam("authId") Long authId);
}
