package com.sobok.deliveryservice.delivery.client;

import com.sobok.deliveryservice.common.config.FeignConfig;
import com.sobok.deliveryservice.delivery.dto.info.UserAddressDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service", configuration = FeignConfig.class)
public interface UserFeignClient {

    /**
     * addressId로 주소 정보 조회
     */
    @GetMapping("/api/findUserAddress")
    ResponseEntity<List<UserAddressDto>> getUserAddressInfo(@RequestParam List<Long> id);
}
