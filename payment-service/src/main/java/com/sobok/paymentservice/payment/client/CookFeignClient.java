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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "cook-service", configuration = FeignConfig.class)
public interface CookFeignClient {

    @GetMapping("/api/get-cook-default-ingre")
    ResponseEntity<Map<Long, Integer>> getDefaultIngreInfoList(@RequestParam Long cookId);

    // 장바구니 조회용
    @GetMapping("/api/cook/{cookId}")
    ResponseEntity<List<CookDetailResDto>> getCookDetail(@PathVariable List<Long> cookId);

    // 식재료 조회용
    @GetMapping("/api/ingredients/{id}")
    ResponseEntity<List<IngredientResDto>> getIngredients(@PathVariable("id") List<Long> ingreId);

    // 주문 내역 조회용
    @GetMapping("/api/cooks-info")
    ResponseEntity<List<CookInfoResDto>> getCooksInfo(@RequestParam("id") List<Long> cookIds);

    /**
     * 요리 ID로 요리 이름을 조회
     */
    @GetMapping("/api/cook-name")
    ResponseEntity<String> getCookNameById(@RequestParam Long cookId);

    /**
     * 요리 ID로 기본 식재료 목록 조회
     */
    @GetMapping("/api/cook/base-ingredients")
    ResponseEntity<List<IngredientTwoResDto>> getBaseIngredients(@RequestParam Long cookId);

    /**
     * 식재료 ID로 상세 정보 조회
     */
    @GetMapping("/api/cook/ingredient-info")
    ResponseEntity<IngredientTwoResDto> getIngredientInfo(@RequestParam Long ingreId);

    @PutMapping("/api/monthly-hot")
    void updateMonthlyHotCooks(@RequestBody List<MonthlyHot> monthlyHotList);

    /**
     * 요리 Id 목록을 기반으로 요리 이름 정보를 조회
     */
    @PostMapping("/api/cook-names")
    ResponseEntity<List<CookNameResDto>> getCookNames(@RequestBody List<Long> cookIds);

    /**
     * 식재료 이름(주문조회용)
     */
    @PostMapping("/api/admin/ingredient-names")
    ResponseEntity<List<IngredientNameResDto>> getIngredientNames(@RequestBody List<Long> ingreIds);
}

