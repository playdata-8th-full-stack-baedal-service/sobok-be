package com.sobok.authservice.auth.client;

import com.sobok.authservice.auth.dto.request.ShopSignupReqDto;
import com.sobok.authservice.auth.dto.response.AuthShopResDto;
import com.sobok.authservice.common.config.FeignConfig;
import com.sobok.authservice.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "shop-service", configuration = FeignConfig.class)
public interface ShopServiceClient {

    @PostMapping("/api/register")
    ApiResponse<AuthShopResDto> shopSignup(@RequestBody ShopSignupReqDto shopDto);

}
