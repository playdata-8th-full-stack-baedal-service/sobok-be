package com.sobok.paymentservice.payment.client;

import com.sobok.paymentservice.common.config.FeignConfig;
import com.sobok.paymentservice.payment.dto.response.CookDetailResDto;
import com.sobok.paymentservice.payment.dto.response.IngredientResDto;
import com.sobok.paymentservice.payment.dto.response.IngredientTwoResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
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

    @GetMapping("/api/cooks")
    List<CookDetailResDto> getCookDetails(@RequestParam("id") List<Long> cookIds);

    /**
     * 요리 ID로 요리 이름을 조회
     */
    @GetMapping("/api/cook/name")
    String getCookNameById(@RequestParam Long cookId);

    /**
     * 요리 ID로 기본 식재료 목록 조회
     */
    @GetMapping("/api/cook/base-ingredients")
    List<IngredientTwoResDto> getBaseIngredients(@RequestParam Long cookId);

    /**
     * 식재료 ID로 상세 정보 조회
     */
    @GetMapping("/api/cook/ingredient-info")
    IngredientTwoResDto getIngredientInfo(@RequestParam Long ingreId);
}

