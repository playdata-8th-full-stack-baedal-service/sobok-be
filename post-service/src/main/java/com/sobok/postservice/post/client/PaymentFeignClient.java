package com.sobok.postservice.post.client;

import com.sobok.postservice.common.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

}