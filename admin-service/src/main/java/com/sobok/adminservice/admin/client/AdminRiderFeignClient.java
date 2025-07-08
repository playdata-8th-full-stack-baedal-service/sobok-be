package com.sobok.adminservice.admin.client;

import com.sobok.adminservice.admin.dto.rider.RiderResDto;
import com.sobok.adminservice.common.config.FeignConfig;
import com.sobok.adminservice.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "delivery-service", configuration = FeignConfig.class)
public interface AdminRiderFeignClient {

    /**
     * 라이더 전체 조회
     */
    @GetMapping("/api/get-rider-all")
    ApiResponse<List<RiderResDto>> getAllRiders();
}