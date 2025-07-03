package com.sobok.authservice.auth.client;

import com.sobok.authservice.auth.dto.info.AuthShopInfoResDto;
import com.sobok.authservice.auth.dto.request.ShopSignupReqDto;
import com.sobok.authservice.auth.dto.response.AuthShopResDto;
import com.sobok.authservice.auth.dto.response.ByPhoneResDto;
import com.sobok.authservice.auth.dto.response.UserResDto;
import com.sobok.authservice.common.config.FeignConfig;
import com.sobok.authservice.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "shop-service", configuration = FeignConfig.class)
public interface ShopServiceClient {

    @PostMapping("/api/register")
    ApiResponse<AuthShopResDto> shopSignup(@RequestBody ShopSignupReqDto shopDto);

    @PostMapping("/api/findByPhoneNumber")
    ApiResponse<ByPhoneResDto> findByPhone(@RequestBody String phoneNumber);

    @GetMapping("/api/shop-info")
    ResponseEntity<AuthShopInfoResDto> getInfo(@RequestParam Long authId);
}
