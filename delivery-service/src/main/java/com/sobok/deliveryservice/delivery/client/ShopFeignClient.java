package com.sobok.deliveryservice.delivery.client;

import com.sobok.deliveryservice.common.config.FeignConfig;
import com.sobok.deliveryservice.delivery.dto.response.DeliveryAvailShopResDto;
import com.sobok.deliveryservice.delivery.dto.response.RiderInfoResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@FeignClient(name = "shop-service", configuration = FeignConfig.class)
public interface ShopFeignClient {

    @GetMapping("/api/find-near-shop")
    ResponseEntity<List<DeliveryAvailShopResDto>> getNearShop(@RequestParam Double latitude, @RequestParam Double longitude);

    @GetMapping("/api/find-shopInfo")
    ResponseEntity<List<DeliveryAvailShopResDto>> getShopInfoByIds(@RequestParam List<Long> ids);
}
