package com.sobok.shopservice.shop.controller;


import com.sobok.shopservice.common.dto.ApiResponse;
import com.sobok.shopservice.shop.dto.request.ShopSignupReqDto;
import com.sobok.shopservice.shop.dto.response.AuthShopResDto;
import com.sobok.shopservice.shop.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

}
