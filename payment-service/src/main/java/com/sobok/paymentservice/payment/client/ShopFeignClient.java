package com.sobok.paymentservice.payment.client;

import com.sobok.paymentservice.common.config.FeignConfig;
import com.sobok.paymentservice.payment.dto.payment.ShopAssignDto;
import com.sobok.paymentservice.payment.dto.shop.AdminShopResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "shop-service", configuration = FeignConfig.class)
public interface ShopFeignClient {

    @PostMapping("/api/assign-shop")
    void assignNearestShop(@RequestBody ShopAssignDto reqDto);

    /**
     * 주문 전체 조회용 가게 정보
     */
    @GetMapping("/api/shop-info-all")
    ResponseEntity<AdminShopResDto> getShopInfo(@RequestParam Long shopId);
}
