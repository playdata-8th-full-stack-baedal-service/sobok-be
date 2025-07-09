package com.sobok.adminservice.admin.client;

import com.sobok.adminservice.admin.dto.order.AdminPaymentResDto;
import com.sobok.adminservice.admin.dto.order.CartCookResDto;
import com.sobok.adminservice.admin.dto.order.CartIngredientResDto;
import com.sobok.adminservice.common.config.FeignConfig;
import com.sobok.adminservice.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "payment-service", configuration = FeignConfig.class)
public interface AdminPaymentFeignClient {

    /**
     * 결제 정보 조회
     */
    @GetMapping("/api/admin/payments")
    ApiResponse<List<AdminPaymentResDto>> getAllPayments();

    /**
     * 결제 id로 요리 조회
     */
    @GetMapping("/api/admin/cook-ids")
    List<Long> getCookIdsByPaymentId(@RequestParam Long paymentId);

    /**
     * paymentId로 cart_cook 리스트 조회
     */
    @GetMapping("/api/admin/cart-cooks")
    List<CartCookResDto> getCartCooks(@RequestParam Long paymentId);

    /**
     * cartCookId로 cart_ingredient 리스트 조회
     */
    @GetMapping("/api/admin/cart-ingredients")
    List<CartIngredientResDto> getCartIngredients(@RequestParam Long cartCookId);
}

