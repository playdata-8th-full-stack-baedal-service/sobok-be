package com.sobok.deliveryservice.delivery.controller;

import com.sobok.deliveryservice.common.dto.ApiResponse;
import com.sobok.deliveryservice.common.dto.TokenUserInfo;
import com.sobok.deliveryservice.common.exception.CustomException;
import com.sobok.deliveryservice.delivery.dto.info.AuthRiderInfoResDto;
import com.sobok.deliveryservice.delivery.dto.payment.DeliveryRegisterDto;
import com.sobok.deliveryservice.delivery.dto.payment.RiderPaymentInfoResDto;
import com.sobok.deliveryservice.delivery.dto.request.AcceptOrderReqDto;
import com.sobok.deliveryservice.delivery.dto.request.RiderReqDto;
import com.sobok.deliveryservice.delivery.dto.response.ByPhoneResDto;
import com.sobok.deliveryservice.delivery.dto.response.DeliveryResDto;
import com.sobok.deliveryservice.delivery.dto.response.RiderInfoResDto;
import com.sobok.deliveryservice.delivery.dto.response.RiderResDto;
import com.sobok.deliveryservice.delivery.entity.Delivery;
import com.sobok.deliveryservice.delivery.repository.DeliveryRepository;
import com.sobok.deliveryservice.delivery.repository.RiderRepository;
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
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class DeliveryFeignController {

    private final DeliveryService deliveryService;
    private final RiderService riderService;

    @PostMapping("/signup")
    public ResponseEntity<RiderResDto> signup(@RequestBody @Valid RiderReqDto dto) {
        log.info("rider signup 요청 들어옴");
        RiderResDto response = riderService.riderCreate(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/findByPhoneNumber")
    public ResponseEntity<?> getUser(@RequestBody String phoneNumber) {
        ByPhoneResDto byPhoneNumber = riderService.findByPhoneNumber(phoneNumber);
        log.info("검색한 사용자 정보 with phone number: {}", byPhoneNumber);
        return ResponseEntity.ok().body(ApiResponse.ok(byPhoneNumber, "전화번호로 찾은 rider 정보입니다."));

    }

    /**
     * 라이더 면허 번호 중복 검증
     */
    @GetMapping("/check-permission")
    public ResponseEntity<Boolean> checkPermission(@RequestParam String permission) {
        return ResponseEntity.ok(riderService.existsByPermissionNumber(permission));
    }

    @GetMapping("/rider-info")
    public ResponseEntity<AuthRiderInfoResDto> getInfo(@RequestParam Long authId) {
        AuthRiderInfoResDto resDto = riderService.getInfo(authId);
        return ResponseEntity.ok().body(resDto);
    }

    @PostMapping("/register-delivery")
    public void registerDelivery(@RequestBody DeliveryRegisterDto reqDto) {
        deliveryService.registerDelivery(reqDto);
    }

    @GetMapping("/get-rider-id")
    public Long getRiderId(@RequestParam Long id) {
        return riderService.getRiderId(id);
    }

    /**
     * 결제관련 라이더 정보 조회
     */
    @GetMapping("/admin/rider-info")
    public ResponseEntity<RiderPaymentInfoResDto> getRiderPaymentInfo(@RequestParam Long paymentId) {
        return ResponseEntity.ok().body(deliveryService.getRiderInfoByPaymentId(paymentId));
    }

    /**
     * paymentId를 기준으로 delivery 테이블에서 shopId를 찾아서 반환
     */
    @GetMapping("/shop-id/payment")
    public ResponseEntity<Long> getShopIdByPaymentId(@RequestParam Long paymentId) {
        Long shopId = deliveryService.getShopIdByPaymentId(paymentId);
        return ResponseEntity.ok().body(shopId);
    }

    /**
     * 주문 상세 조회에서 사용되는 paymentId로 배달 정보 조회
     */
    @GetMapping("/getDelivery")
    public ResponseEntity<DeliveryResDto> getDelivery(@RequestParam Long paymentId) {
        return ResponseEntity.ok().body(deliveryService.getDelivery(paymentId));
    }

    /**
     * 가게용 주문 전체 조회에서 사용되는 shopId로 paymentId 조회
     */
    @GetMapping("/getPaymentId")
    public ResponseEntity<List<Long>> getPaymentId(@RequestParam("shopId") Long shopId) {
        return ResponseEntity.ok().body(deliveryService.getPaymentId(shopId));
    }



    /**
     * 라이더 주문 수락
     */
    @PostMapping("/accept-delivery")
    public void acceptOrder(@RequestBody AcceptOrderReqDto acceptOrderReqDto) {
        deliveryService.acceptDelivery(acceptOrderReqDto);
    }

    /**
     * 라이더 배달 완료
     */
    @PostMapping("/complete-delivery")
    public void deliveryComplete(@RequestBody AcceptOrderReqDto acceptOrderReqDto) {
        deliveryService.deliveryComplete(acceptOrderReqDto);
    }
}
