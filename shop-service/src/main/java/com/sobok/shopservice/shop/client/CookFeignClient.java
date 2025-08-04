package com.sobok.shopservice.shop.client;

import com.sobok.shopservice.common.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "cook-service", url = "${COOK_SERVICE_URL}", configuration = FeignConfig.class)
public interface CookFeignClient {
    /**
     * 식재료 ID로 상세 정보 조회
     */
    @GetMapping("/api/exist-ingre")
    Boolean existIngredient(@RequestParam Long ingreId);

    @GetMapping("/api/get-names")
    ResponseEntity<Map<Long, String>> getNames(@RequestParam List<Long> ids);
}

