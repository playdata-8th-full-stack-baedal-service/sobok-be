package com.sobok.shopservice.shop.controller;

import com.sobok.shopservice.common.dto.ApiResponse;
import com.sobok.shopservice.shop.dto.info.AuthShopInfoResDto;
import com.sobok.shopservice.shop.dto.request.ShopSignupReqDto;
import com.sobok.shopservice.shop.dto.response.AuthShopResDto;
import com.sobok.shopservice.shop.dto.response.ByPhoneResDto;
import com.sobok.shopservice.shop.repository.ShopRepository;
import com.sobok.shopservice.shop.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class ShopFeignController {

    private final ShopService shopService;
    private final ShopRepository shopRepository;

    @PostMapping("/register")
    public ResponseEntity<?> registerShop(@RequestBody ShopSignupReqDto shopSignupReqDto) {
        AuthShopResDto shop = shopService.createShop(shopSignupReqDto);
        return ResponseEntity.ok().body(ApiResponse.ok(shop, "가게가 등록되었습니다."));
    }

    @PostMapping("/findByPhoneNumber")
    public ResponseEntity<?> getUser(@RequestBody String phoneNumber) {
        ByPhoneResDto byPhoneNumber = shopService.findByPhoneNumber(phoneNumber);
        log.info("검색한 사용자 정보 with phone number: {}", byPhoneNumber);
        return ResponseEntity.ok().body(ApiResponse.ok(byPhoneNumber, "전화번호로 찾은 shop 정보입니다."));

    }


    /**
     * 가게 이름 중복 확인
     */
    @GetMapping("/check-shopName")
    public ResponseEntity<Boolean> checkShopName(@RequestParam String shopName) {
        return ResponseEntity.ok((shopRepository.existsByShopName(shopName)));
    }
    /**
     * 가게 주소 중복 확인
     */
    @GetMapping("/check-shopAddress")
    public ResponseEntity<Boolean> checkShopAddress(@RequestParam String shopAddress) {
        return ResponseEntity.ok(shopRepository.existsByRoadFull((shopAddress)));


    @GetMapping("/shop-info")
    public ResponseEntity<AuthShopInfoResDto> getInfo(@RequestParam Long authId) {
        AuthShopInfoResDto resDto = shopService.getInfo(authId);
        return ResponseEntity.ok().body(resDto);
    }
}
