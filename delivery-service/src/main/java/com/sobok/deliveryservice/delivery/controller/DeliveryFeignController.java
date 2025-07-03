package com.sobok.deliveryservice.delivery.controller;

import com.sobok.deliveryservice.common.dto.ApiResponse;
import com.sobok.deliveryservice.delivery.dto.info.AuthRiderInfoResDto;
import com.sobok.deliveryservice.delivery.dto.response.ByPhoneResDto;
import com.sobok.deliveryservice.delivery.dto.response.RiderResDto;
import com.sobok.deliveryservice.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class DeliveryFeignController {

    private final DeliveryService deliveryService;

    @PostMapping("/findByPhoneNumber")
    public ResponseEntity<?> getUser(@RequestBody String phoneNumber) {
        ByPhoneResDto byPhoneNumber = deliveryService.findByPhoneNumber(phoneNumber);
        log.info("검색한 사용자 정보 with phone number: {}", byPhoneNumber);
        return ResponseEntity.ok().body(ApiResponse.ok(byPhoneNumber, "전화번호로 찾은 rider 정보입니다."));

    }


    @GetMapping("/rider-info")
    public ResponseEntity<AuthRiderInfoResDto> getInfo(@RequestParam Long authId) {
        AuthRiderInfoResDto resDto = deliveryService.getInfo(authId);
        return ResponseEntity.ok().body(resDto);
    }
}
