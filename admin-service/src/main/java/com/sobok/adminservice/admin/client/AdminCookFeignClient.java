package com.sobok.adminservice.admin.client;

import com.sobok.adminservice.admin.dto.order.CookNameResDto;
import com.sobok.adminservice.admin.dto.order.IngredientNameResDto;
import com.sobok.adminservice.common.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "cook-service", configuration = FeignConfig.class)
public interface AdminCookFeignClient {

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