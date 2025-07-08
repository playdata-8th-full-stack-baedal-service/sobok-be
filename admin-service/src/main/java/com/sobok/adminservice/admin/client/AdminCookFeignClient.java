package com.sobok.adminservice.admin.client;

import com.sobok.adminservice.admin.dto.order.CookNameResDto;
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
}