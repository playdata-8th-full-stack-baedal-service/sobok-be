package com.sobok.postservice.post.client;

import com.sobok.postservice.common.config.FeignConfig;
import com.sobok.postservice.post.dto.response.IngredientResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "payment-service", url = "${PAYMENT_SERVICE_URL}", configuration = FeignConfig.class)
public interface PaymentFeignClient {

    /**
     * 주어진 paymentId가 해당 userId의 결제 완료 상태인지 여부를 확인
     */
    @GetMapping("/api/payment/completed")
    Boolean isPaymentCompleted(@RequestParam Long paymentId, @RequestParam Long userId);

    /**
     * 결제 ID에 연결된 요리 cookId를 반환
     */
    @GetMapping("/api/payment/cook-id")
    Long getCookIdByPaymentId(@RequestParam Long paymentId);

    /**
     * 요리 ID로 요리 이름을 조회
     */
    @GetMapping("/api/payment/cook-name")
    String getCookName(@RequestParam Long cookId);

    /**
     * 기본 식재료 목록 조회 (cookId 기준)
     */
    @GetMapping("/api/payment/default-ingredients")
    List<IngredientResDto> getDefaultIngredients(@RequestParam Long cookId);

    /**
     * 추가 식재료 목록 조회 (cartCookId 기준)
     */
    @GetMapping("/api/payment/extra-ingredients")
    List<IngredientResDto> getExtraIngredients(@RequestParam Long cartCookId);

    /**
     * 결제 ID에 연결된 CartCook ID 조회
     */
    @GetMapping("/api/payment/cart-cook-id")
    Long getCartCookIdByPaymentId(@RequestParam Long paymentId);

}