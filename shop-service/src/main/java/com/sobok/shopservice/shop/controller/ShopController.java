package com.sobok.shopservice.shop.controller;


import com.sobok.shopservice.common.dto.CommonResponse;
import com.sobok.shopservice.common.dto.TokenUserInfo;

import com.sobok.shopservice.shop.controller.docs.ShopControllerDocs;
import com.sobok.shopservice.shop.dto.response.ShopResDto;
import com.sobok.shopservice.shop.dto.stock.AvailableShopInfoDto;
import com.sobok.shopservice.shop.dto.stock.IngredientIdListDto;
import com.sobok.shopservice.shop.dto.response.ShopPaymentResDto;
import com.sobok.shopservice.shop.service.ShopService;
import com.sobok.shopservice.shop.service.ShopAvailabilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shop")
@Slf4j
@RequiredArgsConstructor
public class ShopController implements ShopControllerDocs {

    private final ShopService shopService;
    private final ShopAvailabilityService shopAvailabilityService;

    /**
     * 가게 이름 중복 확인
     */
    @GetMapping("/check-shopName")
    public ResponseEntity<?> checkShopName(@RequestParam String shopName) {
        shopService.checkShopName(shopName);
        return ResponseEntity.ok(CommonResponse.ok(null, "사용 가능한 지점명 입니다."));
    }

    /**
     * 가게 주소 중복 확인
     */
    @GetMapping("/check-shopAddress")
    public ResponseEntity<?> checkShopAddress(@RequestParam String shopAddress) {
        shopService.checkShopAddress(shopAddress);
        return ResponseEntity.ok(CommonResponse.ok(null, "사용 가능한 주소 입니다."));
    }

    /**
     * 가게에 들어온 전체 주문 조회 (최신순)
     */
    @GetMapping("/all-order")
    public ResponseEntity<?> getAllOrders(@AuthenticationPrincipal TokenUserInfo userInfo
                                         ) {
        List<ShopPaymentResDto> allOrders = shopService.getAllOrders(userInfo);
        if (allOrders.isEmpty()) {
            return ResponseEntity.ok().body(CommonResponse.ok(null, HttpStatus.NO_CONTENT));
        }
        return ResponseEntity.ok(CommonResponse.ok(allOrders, "들어온 모든 주문 목록을 조회하였습니다."));
    }

    /**
     * 가게에 들어온 주문을 주문 상태에 따라 필터링 조회 (최신순)
     */
    @GetMapping("/filtering-order")
    public ResponseEntity<?> getFilterOrders(@AuthenticationPrincipal TokenUserInfo userInfo, @RequestParam String orderState) {
        List<ShopPaymentResDto> allOrders = shopService.getFilteringOrders(userInfo, orderState);
        if (allOrders.isEmpty()) {
            return ResponseEntity.ok().body(CommonResponse.ok(null, HttpStatus.NO_CONTENT));
        }
        return ResponseEntity.ok(CommonResponse.ok(allOrders, "주문 목록을 상태별로 조회하였습니다."));
    }

    @PostMapping("/available")
    public ResponseEntity<?> getAvailableShopList(
            @RequestParam Long addressId,
            @RequestBody IngredientIdListDto reqDto
    ) {
        List<AvailableShopInfoDto> result = shopAvailabilityService.getAvailableShopList(addressId, reqDto);
        return ResponseEntity.ok().body(CommonResponse.ok(result, "가능한 가게 정보를 성공적으로 조회하였습니다."));
    }

    /**
     * 관리자 전용 가게 전체 조회
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllShops(@AuthenticationPrincipal TokenUserInfo userInfo) {
        List<ShopResDto> result = shopService.getAllShops();
        return ResponseEntity.ok(CommonResponse.ok(result, "가게 전체 조회 성공"));
    }
}
