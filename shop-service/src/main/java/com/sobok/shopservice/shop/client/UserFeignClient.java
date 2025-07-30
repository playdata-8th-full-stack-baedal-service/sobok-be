package com.sobok.shopservice.shop.client;

import com.sobok.shopservice.common.config.FeignConfig;
import com.sobok.shopservice.shop.dto.payment.LocationResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", url = "${USER_SERVICE_URL}", configuration = FeignConfig.class)
public interface UserFeignClient {

    @GetMapping("/api/get-user-address")
    LocationResDto getUserAddress(@RequestParam Long userAddressId);
}
