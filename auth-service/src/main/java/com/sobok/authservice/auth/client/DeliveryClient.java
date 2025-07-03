package com.sobok.authservice.auth.client;

import com.sobok.authservice.auth.dto.info.AuthRiderInfoResDto;
import com.sobok.authservice.auth.dto.request.RiderReqDto;
import com.sobok.authservice.auth.dto.response.ByPhoneResDto;
import com.sobok.authservice.auth.dto.response.UserResDto;
import com.sobok.authservice.common.config.FeignConfig;
import com.sobok.authservice.auth.dto.response.AuthRiderResDto;
import com.sobok.authservice.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

// 라이더 회원가입 feign 처리
@FeignClient(name = "delivery-service", configuration = FeignConfig.class)
public interface DeliveryClient {

    @PostMapping("/delivery/signup")
    ApiResponse<AuthRiderResDto> registerRider(@RequestBody RiderReqDto dto);

    @PostMapping("/api/findByPhoneNumber")
    ApiResponse<ByPhoneResDto> findByPhone(@RequestBody String phoneNumber);

    @GetMapping("/api/rider-info")
    ResponseEntity<AuthRiderInfoResDto> getInfo(@RequestParam Long authId);
}
