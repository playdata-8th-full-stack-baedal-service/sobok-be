package com.sobok.adminservice.admin.client;

import com.sobok.adminservice.admin.dto.order.AdminPaymentResDto;
import com.sobok.adminservice.common.config.FeignConfig;
import com.sobok.adminservice.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "payment-service", configuration = FeignConfig.class)
public interface AdminPaymentFeignClient {

    /**
     * 결제 정보 조회
     */
    @GetMapping("/api/admin/payments")
    ApiResponse<List<AdminPaymentResDto>> getAllPayments();
}
