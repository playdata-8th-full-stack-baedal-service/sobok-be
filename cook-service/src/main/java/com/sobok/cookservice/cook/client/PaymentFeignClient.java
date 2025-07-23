package com.sobok.cookservice.cook.client;

import com.sobok.cookservice.common.config.FeignConfig;
import com.sobok.cookservice.cook.dto.response.CartMonthlyHotDto;
import com.sobok.cookservice.cook.dto.response.CookOrderCountDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "payment-service", configuration = FeignConfig.class)
public interface PaymentFeignClient {

    @GetMapping("/api/popular-cook-ids")
    ResponseEntity<List<CookOrderCountDto>> getPopularCookIds(@RequestParam int page,
                                                             @RequestParam int size);


    @GetMapping("/api/monthly-hot")
    CartMonthlyHotDto getMonthlyHotCooks(@RequestParam int pageNo, @RequestParam int numOfRows);
}