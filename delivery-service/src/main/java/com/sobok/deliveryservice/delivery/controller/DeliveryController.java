package com.sobok.deliveryservice.delivery.controller;


import com.sobok.deliveryservice.common.dto.ApiResponse;
import com.sobok.deliveryservice.common.dto.TokenUserInfo;
import com.sobok.deliveryservice.delivery.dto.request.AcceptOrderReqDto;
import com.sobok.deliveryservice.delivery.dto.request.RiderReqDto;
import com.sobok.deliveryservice.delivery.dto.response.DeliveryAvailOrderResDto;
import com.sobok.deliveryservice.delivery.dto.response.DeliveryOrderResDto;
import com.sobok.deliveryservice.delivery.dto.response.RiderInfoResDto;
import com.sobok.deliveryservice.delivery.dto.response.RiderResDto;
import com.sobok.deliveryservice.delivery.service.DeliveryService;
import com.sobok.deliveryservice.delivery.service.RiderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    private final RiderService riderService;

    /**
     * 라이더 면허 번호 중복 확인
     */
    @GetMapping("/check-permission")
    public ResponseEntity<?> checkPermission(@RequestParam String permission) {
        riderService.checkPermission(permission);
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
        if (availableOrders.isEmpty()) {
            return ResponseEntity.ok().body(ApiResponse.ok(null, HttpStatus.NO_CONTENT));
        }
        return ResponseEntity.ok(ApiResponse.ok(availableOrders, "배달 가능한 주문 목록을 조회하였습니다."));
    }

    /**
     * 라이더 배달 중인 주문 조회
     */
    @GetMapping("/delivering-order")
    public ResponseEntity<?> getDeliveringOrders(@AuthenticationPrincipal TokenUserInfo userInfo
            , @RequestParam Long pageNo, @RequestParam Long numOfRows) {
        List<DeliveryOrderResDto> deliveringOrders = deliveryService.getDeliveringOrders(userInfo, pageNo, numOfRows);
        return ResponseEntity.ok(ApiResponse.ok(deliveringOrders, "배달 중인 목록을 조회하였습니다."));

    }

    /**
     * 라이더의 모든 배달 리스트 조회
     */
    @GetMapping("/delivery-list")
    public ResponseEntity<?> getDeliveryOrders(@AuthenticationPrincipal TokenUserInfo userInfo
            , @RequestParam Long pageNo, @RequestParam Long numOfRows) {
        List<DeliveryOrderResDto> deliveryOrders = deliveryService.getDeliveryOrders(userInfo, pageNo, numOfRows);
        return ResponseEntity.ok(ApiResponse.ok(deliveryOrders, "배달 전체 목록을 조회하였습니다."));
    }

    /**
     * 관리자 전용 라이더 전체 조회
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllRiders(@AuthenticationPrincipal TokenUserInfo userInfo) {
        List<RiderInfoResDto> riders = riderService.getAllRiders();
        return ResponseEntity.ok(ApiResponse.ok(riders, "전체 라이더 정보 조회 성공"));
    }

    /**
     * 관리자 전용 승인 대기중인 라이더 전체 조회
     */
    @GetMapping("/pending-rider")
    public ResponseEntity<?> getPendingRiders(@AuthenticationPrincipal TokenUserInfo userInfo) {
        List<RiderResDto> pendingRiders = riderService.getPendingRiders();
        return ResponseEntity.ok(ApiResponse.ok(pendingRiders, "비활성화된 라이더 정보 조회 성공."));
    }

}
