package com.sobok.shopservice.shop.controller;


import com.sobok.shopservice.common.dto.ApiResponse;
import com.sobok.shopservice.shop.dto.request.ShopSignupReqDto;
import com.sobok.shopservice.shop.dto.response.AuthShopResDto;
import com.sobok.shopservice.shop.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shop")
@Slf4j
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    @PostMapping("/register")
    public ResponseEntity<?> registerShop(@RequestBody ShopSignupReqDto shopSignupReqDto) {
        AuthShopResDto shop = shopService.createShop(shopSignupReqDto);
        return ResponseEntity.ok().body(ApiResponse.ok(shop, "가게가 등록되었습니다."));
    }


}
