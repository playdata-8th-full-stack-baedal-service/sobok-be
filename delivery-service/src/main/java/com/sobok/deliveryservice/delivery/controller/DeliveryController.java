package com.sobok.deliveryservice.delivery.controller;


import com.sobok.deliveryservice.common.dto.ApiResponse;
import com.sobok.deliveryservice.common.dto.TokenUserInfo;
import com.sobok.deliveryservice.delivery.dto.request.AcceptOrderReqDto;
import com.sobok.deliveryservice.delivery.dto.request.RiderReqDto;
import com.sobok.deliveryservice.delivery.dto.response.DeliveryAvailOrderResDto;
import com.sobok.deliveryservice.delivery.dto.response.RiderResDto;
import com.sobok.deliveryservice.delivery.service.DeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/delivery")
public class DeliveryController {

    private final DeliveryService deliveryService;

    /**
     * 라이더 면허 번호 중복 확인
     */
    @GetMapping("/check-permission")
    public ResponseEntity<?> checkPermission(@RequestParam String permission) {
        deliveryService.checkPermission(permission);
        return ResponseEntity.ok(ApiResponse.ok(null, "사용 가능한 면허번호 입니다."));
    }

    /**
     * 라이더 배달 가능 주문 조회
     */
    @GetMapping("/available-order")
    public ResponseEntity<?> getAllOrders(@AuthenticationPrincipal TokenUserInfo userInfo,
                                          @RequestParam Double latitude, @RequestParam Double longitude,
                                          @RequestParam Long pageNo, @RequestParam Long numOfRows) {
        List<DeliveryAvailOrderResDto> availableOrders = deliveryService.getAvailableOrders(userInfo, latitude, longitude, pageNo, numOfRows);
        return ResponseEntity.ok(ApiResponse.ok(availableOrders, "배달 가능한 주문 목록을 조회하였습니다."));
    }

    /**
     * 라이더 주문 수락
     */
    @PatchMapping("/accept-delivery")
    public ResponseEntity<?> acceptOrder(@AuthenticationPrincipal TokenUserInfo userInfo, @RequestBody AcceptOrderReqDto acceptOrderReqDto) {
        deliveryService.acceptDelivery(userInfo, acceptOrderReqDto);
        return ResponseEntity.ok(ApiResponse.ok( "배달을 수락하였습니다."));
    }
}
