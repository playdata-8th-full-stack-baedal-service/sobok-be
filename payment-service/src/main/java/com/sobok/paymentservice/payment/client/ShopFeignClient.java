package com.sobok.paymentservice.payment.client;

import com.sobok.paymentservice.common.config.FeignConfig;
import com.sobok.paymentservice.payment.dto.payment.ShopAssignDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "shop-service", configuration = FeignConfig.class)
public interface ShopFeignClient {

    @GetMapping("/api/assign-shop")
    void assignNearestShop(@RequestBody ShopAssignDto reqDto);

}
