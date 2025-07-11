package com.sobok.deliveryservice.delivery.controller;


import com.sobok.deliveryservice.common.dto.ApiResponse;
import com.sobok.deliveryservice.delivery.dto.request.RiderReqDto;
import com.sobok.deliveryservice.delivery.dto.response.RiderResDto;
import com.sobok.deliveryservice.delivery.service.DeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
