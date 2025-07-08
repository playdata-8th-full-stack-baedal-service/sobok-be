package com.sobok.adminservice.admin.client;


import com.sobok.adminservice.admin.dto.order.AdminShopResDto;
import com.sobok.adminservice.admin.dto.shop.ShopResDto;
import com.sobok.adminservice.common.config.FeignConfig;
import com.sobok.adminservice.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "shop-service",  configuration = FeignConfig.class)
public interface AdminShopFeignClient {

    @GetMapping("/api/shop-all")
    ApiResponse<List<ShopResDto>> getAllShops();

    /**
     * 가게 정보용
     */
    @GetMapping("/api/shop-info-all")
    AdminShopResDto getShopInfo(@RequestParam Long shopId);
}