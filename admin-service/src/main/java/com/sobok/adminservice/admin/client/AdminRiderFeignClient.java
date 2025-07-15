package com.sobok.adminservice.admin.client;

import com.sobok.adminservice.admin.dto.order.RiderPaymentInfoResDto;
import com.sobok.adminservice.admin.dto.rider.PendingRiderResDto;
import com.sobok.adminservice.admin.dto.rider.RiderResDto;
import com.sobok.adminservice.common.config.FeignConfig;
import com.sobok.adminservice.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "delivery-service", configuration = FeignConfig.class)
public interface AdminRiderFeignClient {

    /**
     * 미승인 라이더 조회
     */
    @GetMapping("/api/get-pending-rider")
    List<PendingRiderResDto> getPendingRiders();

    /**
     * 라이더 전체 조회
     */
    @GetMapping("/api/get-rider-all")
    ApiResponse<List<RiderResDto>> getAllRiders();

    /**
     * 라이더 정보 조회
     */
    @GetMapping("/api/admin/rider-info")
    RiderPaymentInfoResDto getRiderName(@RequestParam Long paymentId);

    /**
     * paymentId 기준으로 delivery 테이블의 shopId 추출용
     */
    @GetMapping("/api/shop-id/payment")
    Long getShopIdByPaymentId(@RequestParam Long paymentId);

}