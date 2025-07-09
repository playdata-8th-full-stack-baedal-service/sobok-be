package com.sobok.adminservice.admin.client;

import com.sobok.adminservice.admin.dto.order.UserInfoResDto;
import com.sobok.adminservice.common.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", configuration = FeignConfig.class)
public interface UserFeignClient {

    /**
     * 유저 정보 조회
     */
    @GetMapping("/api/admin/user-info")
    UserInfoResDto getUserInfo(@RequestParam("userAddressId") Long userAddressId);

    /**
     * 유저 정보 조회
     */
    @GetMapping("/api/user-id")
    Long getUserIdByUserAddressId(@RequestParam Long userAddressId);

    /**
     * 유저 정보 조회
     */
    @GetMapping("/api/auth-id")
    Long getAuthIdByUserId(@RequestParam Long userId);
}