package com.sobok.shopservice.shop.client;

import com.sobok.shopservice.common.config.FeignConfig;
import com.sobok.shopservice.shop.dto.response.UserLocationResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "api-service", configuration = FeignConfig.class)
public interface ApiServiceClient {

    @GetMapping("/api/convert-addr")
    UserLocationResDto convertAddress(@RequestParam String roadFull);

}
