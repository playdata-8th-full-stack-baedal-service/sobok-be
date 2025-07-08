package com.sobok.deliveryservice.delivery.controller;

import com.sobok.deliveryservice.common.dto.ApiResponse;
import com.sobok.deliveryservice.common.dto.TokenUserInfo;
import com.sobok.deliveryservice.common.exception.CustomException;
import com.sobok.deliveryservice.delivery.dto.info.AuthRiderInfoResDto;
import com.sobok.deliveryservice.delivery.dto.payment.DeliveryRegisterDto;
import com.sobok.deliveryservice.delivery.dto.payment.RiderNameResDto;
import com.sobok.deliveryservice.delivery.dto.response.ByPhoneResDto;
import com.sobok.deliveryservice.delivery.dto.response.RiderInfoResDto;
import com.sobok.deliveryservice.delivery.dto.response.RiderResDto;
import com.sobok.deliveryservice.delivery.entity.Delivery;
import com.sobok.deliveryservice.delivery.repository.DeliveryRepository;
import com.sobok.deliveryservice.delivery.repository.RiderRepository;
import com.sobok.deliveryservice.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class DeliveryFeignController {

    private final DeliveryService deliveryService;
    private final RiderRepository riderRepository;
    private final DeliveryRepository deliveryRepository;


    @PostMapping("/findByPhoneNumber")
    public ResponseEntity<?> getUser(@RequestBody String phoneNumber) {
        ByPhoneResDto byPhoneNumber = deliveryService.findByPhoneNumber(phoneNumber);
        log.info("검색한 사용자 정보 with phone number: {}", byPhoneNumber);
        return ResponseEntity.ok().body(ApiResponse.ok(byPhoneNumber, "전화번호로 찾은 rider 정보입니다."));

    }

    /**
     * 라이더 면허 번호 중복 검증
     */
    @GetMapping("/check-permission")
    public ResponseEntity<Boolean> checkPermission(@RequestParam String permission) {
        return ResponseEntity.ok((riderRepository.existsByPermissionNumber(permission)));

    }


    @GetMapping("/rider-info")
    public ResponseEntity<AuthRiderInfoResDto> getInfo(@RequestParam Long authId) {
        AuthRiderInfoResDto resDto = deliveryService.getInfo(authId);
        return ResponseEntity.ok().body(resDto);
    }

    @PostMapping("/register-delivery")
    public void registerDelivery(@RequestBody DeliveryRegisterDto reqDto) {
        deliveryService.registerDelivery(reqDto);
    }

    @GetMapping("/get-rider-id")
    public Long getRiderId(@RequestParam Long id) {
        return deliveryService.getRiderId(id);
    }

    /**
     * 라이더 정보 조회
     */
    @GetMapping("/get-rider-all")
    public ResponseEntity<?> getAllRiders() {
        List<RiderInfoResDto> riders = deliveryService.getAllRiders();
        return ResponseEntity.ok(ApiResponse.ok(riders, "전체 라이더 조회 성공"));
    }

    /**
     * 라이더 이름 조회
     */
    @GetMapping("/admin/rider-name")
    public RiderNameResDto getRiderName(@RequestParam("paymentId") Long paymentId) {
        return deliveryService.getRiderNameByPaymentId(paymentId);
    }

    /**
     * paymentId를 기준으로 delivery 테이블에서 shopId를 찾아서 반환
     */
    @GetMapping("/shop-id/by-payment")
    public ResponseEntity<Long> getShopIdByPaymentId(@RequestParam Long paymentId) {
        Delivery delivery = deliveryRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new CustomException("배달 정보가 없습니다.", HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(delivery.getShopId());
    }


}
