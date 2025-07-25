package com.sobok.adminservice.admin.controller;


import com.sobok.adminservice.admin.client.AdminFeignClient;
import com.sobok.adminservice.admin.dto.order.AdminPaymentResponseDto;
import com.sobok.adminservice.admin.dto.rider.PendingRiderResDto;
import com.sobok.adminservice.admin.dto.rider.RiderResDto;
import com.sobok.adminservice.admin.dto.shop.ShopResDto;
import com.sobok.adminservice.admin.service.AdminService;
import com.sobok.adminservice.common.dto.ApiResponse;
import com.sobok.adminservice.common.dto.TokenUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final AdminService adminService;

    /**
     * rider 회원가입 승인 요청
     */
    @PutMapping("/rider-active")
    public ResponseEntity<ApiResponse<Void>> activeRider(
            @RequestParam Long authId,
            @AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
        adminService.activateRiderAccount(tokenUserInfo, authId);
        return ResponseEntity.ok(ApiResponse.ok(null, "라이더 계정이 활성화되었습니다."));
    }

    /**
     * 관리자 전용 가게 전체 조회
     */
    @GetMapping("/shops")
    public ResponseEntity<?> getAllShops(@AuthenticationPrincipal TokenUserInfo userInfo) {
        List<ShopResDto> result = adminService.getAllShops(userInfo);
        return ResponseEntity.ok(ApiResponse.ok(result, "가게 전체 조회 성공"));

    }

    /**
     * 관리자 전용 라이더 전체 조회
     */
    @GetMapping("/riders")
    public ResponseEntity<?> getAllRiders(@AuthenticationPrincipal TokenUserInfo userInfo) {
        List<RiderResDto> riders = adminService.getAllRiders(userInfo);
        return ResponseEntity.ok(ApiResponse.ok(riders, "전체 라이더 정보 조회 성공"));
    }

    @GetMapping("/pending-rider")
    public ResponseEntity<?> getPendingRiders(@AuthenticationPrincipal TokenUserInfo userInfo) {
        List<PendingRiderResDto> pendingRiders = adminService.getPendingRiders(userInfo);
        return ResponseEntity.ok(ApiResponse.ok(pendingRiders, "비활성화된 라이더 정보 조회 성공."));
    }

    /**
     * 관리자 전용 사용자 주문 전체 조회
     */
    @GetMapping("/orders")
    public ResponseEntity<?> getAllOrders(@AuthenticationPrincipal TokenUserInfo userInfo,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        Page<AdminPaymentResponseDto> result = adminService.getAllPayments(userInfo, page, size);
        return ResponseEntity.ok(ApiResponse.ok(result, "전체 주문 조회 성공"));
    }

}
