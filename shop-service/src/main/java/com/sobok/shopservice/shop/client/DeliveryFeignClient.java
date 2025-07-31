package com.sobok.shopservice.shop.client;

import com.sobok.shopservice.common.config.FeignConfig;
import com.sobok.shopservice.shop.dto.payment.DeliveryRegisterDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "delivery-service", url = "${DELIVERY_SERVICE_URL}", configuration = FeignConfig.class)
public interface DeliveryFeignClient {
    @PostMapping("/api/register-delivery")
    void registerDelivery(@RequestBody DeliveryRegisterDto deliveryRegisterDto);

    @GetMapping("/api/getPaymentId")
    ResponseEntity<List<Long>> getPaymentId(@RequestParam Long shopId);
}
