package com.sobok.paymentservice.payment.client;

import com.sobok.paymentservice.common.config.FeignConfig;
import com.sobok.paymentservice.payment.dto.cart.MonthlyHot;
import com.sobok.paymentservice.payment.dto.payment.CookNameResDto;
import com.sobok.paymentservice.payment.dto.payment.IngredientNameResDto;
import com.sobok.paymentservice.payment.dto.response.CookDetailResDto;
import com.sobok.paymentservice.payment.dto.response.CookInfoResDto;
import com.sobok.paymentservice.payment.dto.response.IngredientResDto;
import com.sobok.paymentservice.payment.dto.response.IngredientTwoResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "cook-service", configuration = FeignConfig.class)
public interface CookFeignClient {

    @GetMapping("/api/get-cook-default-ingre")
    Map<Long, Integer> getDefaultIngreInfoList(@RequestParam Long cookId);

    // 장바구니 조회용
    @GetMapping("/api/cook/{cookId}")
    List<CookDetailResDto> getCookDetail(@PathVariable List<Long> cookId);

    // 식재료 조회용
    @GetMapping("/api/ingredients/{id}")
    List<IngredientResDto> getIngredients(@PathVariable("id") List<Long> ingreId);

    // 주문 내역 조회용
    @GetMapping("/api/cooks-info")
    List<CookInfoResDto> getCooksInfo(@RequestParam("id") List<Long> cookIds);

    /**
     * 요리 ID로 요리 이름을 조회
     */
    @GetMapping("/api/cook-name")
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

    @PutMapping("/api/monthly-hot")
    void updateMonthlyHotCooks(@RequestBody List<MonthlyHot> monthlyHotList);

    /**
     * 요리 이름 조회(주문 조회용)
     */
    @PostMapping("/api/admin/cook-names")
    List<CookNameResDto> getCookNames(@RequestBody List<Long> cookIds);

    /**
     * 식재료 이름(주문조회용)
     */
    @PostMapping("/api/admin/ingredient-names")
    List<IngredientNameResDto> getIngredientNames(@RequestBody List<Long> ingreIds);
}

