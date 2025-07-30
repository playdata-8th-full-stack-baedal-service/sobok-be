package com.sobok.paymentservice.payment.client;

import com.sobok.paymentservice.common.config.FeignConfig;
import com.sobok.paymentservice.payment.dto.user.UserInfoResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", configuration = FeignConfig.class)
public interface UserServiceClient {

    /**
     * 유저 검증
     */
    @GetMapping("/api/verify-user")
    Boolean verifyUser(@RequestParam Long authId,
                       @RequestParam Long userId);

    /**
     * 유저 정보 조회
     */
    @GetMapping("/api/admin/user-info")
    ResponseEntity<UserInfoResDto> getUserInfo(@RequestParam("userAddressId") Long userAddressId);

    /**
     * 유저 정보 조회
     */
    @GetMapping("/api/user-id")
    ResponseEntity<Long> getUserIdByUserAddressId(@RequestParam Long userAddressId);

    /**
     * 유저 정보 조회
     */
    @GetMapping("/api/auth-id")
    ResponseEntity<Long> getAuthIdByUserId(@RequestParam Long userId);
}