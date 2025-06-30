package com.sobok.authservice.auth.client;

import com.sobok.authservice.auth.dto.request.RiderReqDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// 라이더 회원가입 feign 처리
@FeignClient(name = "delivery-service")
public interface DeliveryClient {

    @PostMapping("/delivery/signup")
    void registerRider(@RequestBody RiderReqDto dto);
}
