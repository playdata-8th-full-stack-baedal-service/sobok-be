package com.sobok.postservice.post.client;

import com.sobok.postservice.common.config.FeignConfig;
import com.sobok.postservice.post.dto.response.IngredientResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "payment-service", configuration = FeignConfig.class)
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

    @GetMapping("/api/payment/cook-name")
    String getCookName(@RequestParam Long cookId);


    @GetMapping("/api/payment/default-ingredients")
    List<IngredientResDto> getDefaultIngredients(@RequestParam Long cookId);

    @GetMapping("/api/payment/extra-ingredients")
    List<IngredientResDto> getExtraIngredients(@RequestParam Long cartCookId);

    @GetMapping("/api/payment/cart-cook-id")
    Long getCartCookIdByPaymentId(@RequestParam Long paymentId);


}