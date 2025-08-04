package com.sobok.paymentservice.payment.client;

import com.sobok.paymentservice.common.config.FeignConfig;
import com.sobok.paymentservice.payment.dto.delivery.AcceptOrderReqDto;
import com.sobok.paymentservice.payment.dto.delivery.DeliveryResDto;
import com.sobok.paymentservice.payment.dto.payment.RiderPaymentInfoResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "delivery-service", url = "${DELIVERY_SERVICE_URL}", configuration = FeignConfig.class)
public interface DeliveryFeignClient {

    //paymentId로 배달 정보 조회
    @GetMapping("/api/getDelivery")
    ResponseEntity<DeliveryResDto> getDelivery(@RequestParam Long paymentId);

    @PostMapping("/api/accept-delivery")
    void assignRider(@RequestBody AcceptOrderReqDto deliveryResDto);

    @PostMapping("/api/complete-delivery")
    void completeDelivery(@RequestBody AcceptOrderReqDto deliveryResDto);

    /**
     * 라이더 정보 조회
     */
    @GetMapping("/api/admin/delivery-info")
    ResponseEntity<RiderPaymentInfoResDto> getDeliveryAndRider(@RequestParam Long paymentId);

    /**
     * paymentId 기준으로 delivery 테이블의 shopId 추출용
     */
    @GetMapping("/api/shop-id/payment")
    ResponseEntity<Long> getShopIdByPaymentId(@RequestParam Long paymentId);
}
