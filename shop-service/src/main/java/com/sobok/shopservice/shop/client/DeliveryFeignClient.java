package com.sobok.shopservice.shop.client;

import com.sobok.shopservice.common.config.FeignConfig;
import com.sobok.shopservice.shop.dto.payment.DeliveryRegisterDto;
import com.sobok.shopservice.shop.dto.payment.LocationResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "delivery-service", configuration = FeignConfig.class)
public interface DeliveryFeignClient {
    @PostMapping("/api/register-delivery")
    void registerDelivery(@RequestBody DeliveryRegisterDto deliveryRegisterDto);
}
