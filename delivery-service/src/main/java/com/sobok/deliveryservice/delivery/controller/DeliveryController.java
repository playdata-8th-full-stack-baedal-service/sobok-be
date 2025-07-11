package com.sobok.deliveryservice.delivery.controller;


import com.sobok.deliveryservice.common.dto.ApiResponse;
import com.sobok.deliveryservice.common.dto.TokenUserInfo;
import com.sobok.deliveryservice.delivery.dto.request.RiderReqDto;
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

    @PostMapping("/signup")
    public ResponseEntity<RiderResDto> signup(@RequestBody @Valid RiderReqDto dto) {
        log.info("rider signup 요청 들어옴");
        RiderResDto response = deliveryService.riderCreate(dto);
        return ResponseEntity.ok(response);
    }

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
        deliveryService.getAvailableOrders(userInfo, latitude, longitude);
        return ResponseEntity.ok(ApiResponse.ok("배달 가능한 주문 목록을 조회하였습니다."));
    }
}
