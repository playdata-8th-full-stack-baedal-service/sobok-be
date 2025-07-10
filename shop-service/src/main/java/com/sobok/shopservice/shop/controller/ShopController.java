package com.sobok.shopservice.shop.controller;


import com.sobok.shopservice.common.dto.ApiResponse;
import com.sobok.shopservice.common.dto.TokenUserInfo;
import com.sobok.shopservice.shop.dto.request.ShopSignupReqDto;
import com.sobok.shopservice.shop.dto.response.AdminShopResDto;
import com.sobok.shopservice.shop.dto.response.AuthShopResDto;
import com.sobok.shopservice.shop.dto.response.ShopPaymentResDto;
import com.sobok.shopservice.shop.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shop")
@Slf4j
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    /**
     * 가게 이름 중복 확인
     */
    @GetMapping("/check-shopName")
    public ResponseEntity<?> checkShopName(@RequestParam String shopName) {
        shopService.checkShopName(shopName);
        return ResponseEntity.ok(ApiResponse.ok(null, "사용 가능한 지점명 입니다."));
    }

    /**
     * 가게 주소 중복 확인
     */
    @GetMapping("/check-shopAddress")
    public ResponseEntity<?> checkShopAddress(@RequestParam String shopAddress) {
        shopService.checkShopAddress(shopAddress);
        return ResponseEntity.ok(ApiResponse.ok(null, "사용 가능한 주소 입니다."));
    }

    /**
     * 가게에 들어온 전체 주문 조회 (최신순)
     */
    @GetMapping("/all-order")
    public ResponseEntity<?> getAllOrders(@AuthenticationPrincipal TokenUserInfo userInfo,
                                          @RequestParam Long pageNo, @RequestParam Long numOfRows) {
        List<ShopPaymentResDto> allOrders = shopService.getAllOrders(userInfo, pageNo, numOfRows);
        return ResponseEntity.ok(ApiResponse.ok(allOrders, "들어온 모든 주문 목록을 조회하였습니다."));
    }

    /**
     * 가게에 들어온 주문을 주문 상태에 따라 필터링 조회 (최신순)
     */
    @GetMapping("/filtering-order")
    public ResponseEntity<?> getFilterOrders(@AuthenticationPrincipal TokenUserInfo userInfo, @RequestParam String orderState,
                                          @RequestParam Long pageNo, @RequestParam Long numOfRows) {
        List<ShopPaymentResDto> allOrders = shopService.getFilteringOrders(userInfo, orderState, pageNo, numOfRows);
        return ResponseEntity.ok(ApiResponse.ok(allOrders, "주문 목록을 상태별로 조회하였습니다."));
    }

}
