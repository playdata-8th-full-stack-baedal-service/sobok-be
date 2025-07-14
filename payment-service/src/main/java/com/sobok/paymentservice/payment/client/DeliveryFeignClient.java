package com.sobok.paymentservice.payment.client;

import com.sobok.paymentservice.common.config.FeignConfig;
import com.sobok.paymentservice.payment.dto.delivery.AcceptOrderReqDto;
import com.sobok.paymentservice.payment.dto.delivery.DeliveryResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "delivery-service", configuration = FeignConfig.class)
public interface DeliveryFeignClient {

    //paymentId로 배달 정보 조회
    @GetMapping("/api/getDelivery")
    DeliveryResDto getDelivery(@RequestParam Long paymentId);

    @PostMapping("/api/accept-delivery")
    void assignRider(@RequestBody AcceptOrderReqDto deliveryResDto);

    @PostMapping("/api/complete-delivery")
    void completeDelivery(@RequestBody AcceptOrderReqDto deliveryResDto);

}
