package com.sobok.deliveryservice.delivery.client;

import com.sobok.deliveryservice.common.config.FeignConfig;
import com.sobok.deliveryservice.delivery.dto.payment.ShopPaymentResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "payment-service", configuration = FeignConfig.class)
public interface PaymentFeignClient {

    @GetMapping("/api/getRiderAvailPayment")
    List<ShopPaymentResDto> getRiderAvailPayment(@RequestParam List<Long> id);

}
