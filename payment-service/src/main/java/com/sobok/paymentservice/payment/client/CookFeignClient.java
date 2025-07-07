package com.sobok.paymentservice.payment.client;

import com.sobok.paymentservice.common.config.FeignConfig;
import com.sobok.paymentservice.payment.dto.response.CookDetailResDto;
import com.sobok.paymentservice.payment.dto.response.IngredientResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "cook-service", configuration = FeignConfig.class)
public interface CookFeignClient {

    @GetMapping("/api/get-cook-default-ingre")
    Map<Long, Integer> getDefaultIngreInfoList(@RequestParam Long cookId);

    @GetMapping("/api/cook/{cookId}")
    CookDetailResDto getCookDetail(@PathVariable Long cookId);

    // 식재료 조회용
    @GetMapping("/api/ingredients/{id}")
    IngredientResDto getIngredient(@PathVariable("id") Long ingreId);


}
