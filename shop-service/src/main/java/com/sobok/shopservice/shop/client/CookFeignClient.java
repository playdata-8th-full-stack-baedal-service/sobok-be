package com.sobok.shopservice.shop.client;

import com.sobok.shopservice.common.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "cook-service", configuration = FeignConfig.class)
public interface CookFeignClient {
    /**
     * 식재료 ID로 상세 정보 조회
     */
    @GetMapping("/api/exist-ingre")
    Boolean existIngredient(@RequestParam Long ingreId);
}

