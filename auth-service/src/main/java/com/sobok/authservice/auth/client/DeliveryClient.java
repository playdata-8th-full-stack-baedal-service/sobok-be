package com.sobok.authservice.auth.client;

import com.sobok.authservice.auth.dto.request.RiderReqDto;
import com.sobok.authservice.common.config.FeignConfig;
import com.sobok.authservice.auth.dto.response.AuthRiderResDto;
import com.sobok.authservice.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// 라이더 회원가입 feign 처리
@FeignClient(name = "delivery-service", configuration = FeignConfig.class)
public interface DeliveryClient {

    @PostMapping("/delivery/signup")
    ApiResponse<AuthRiderResDto> registerRider(@RequestBody RiderReqDto dto);
}
