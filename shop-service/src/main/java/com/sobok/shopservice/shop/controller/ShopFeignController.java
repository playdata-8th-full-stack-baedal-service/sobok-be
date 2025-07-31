package com.sobok.shopservice.shop.controller;

import com.sobok.shopservice.common.dto.ApiResponse;
import com.sobok.shopservice.shop.dto.info.AuthShopInfoResDto;
import com.sobok.shopservice.shop.dto.payment.ShopAssignDto;
import com.sobok.shopservice.shop.dto.request.ShopSignupReqDto;
import com.sobok.shopservice.shop.dto.response.*;
import com.sobok.shopservice.shop.repository.ShopRepository;
import com.sobok.shopservice.shop.service.ShopAssignService;
import com.sobok.shopservice.shop.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class ShopFeignController {

    private final ShopService shopService;
    private final ShopAssignService shopAssignService;
    private final ShopRepository shopRepository;

    @PostMapping("/register")
    public ResponseEntity<?> registerShop(@RequestBody ShopSignupReqDto shopSignupReqDto) {
        AuthShopResDto shop = shopService.createShop(shopSignupReqDto);
        return ResponseEntity.ok().body(shop);
    }

    @PostMapping("/findByPhoneNumber")
    public ResponseEntity<?> getUser(@RequestBody String phoneNumber) {
        ByPhoneResDto byPhoneNumber = shopService.findByPhoneNumber(phoneNumber);
        log.info("검색한 사용자 정보 with phone number: {}", byPhoneNumber);
        return ResponseEntity.ok().body(byPhoneNumber);
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
    }

    @GetMapping("/shop-info")
    public ResponseEntity<AuthShopInfoResDto> getInfo(@RequestParam Long authId) {
        AuthShopInfoResDto resDto = shopService.getInfo(authId);
        return ResponseEntity.ok().body(resDto);
    }

    @PostMapping("/assign-shop")
    public void assignNearestShop(@RequestBody ShopAssignDto reqDto) {
        shopAssignService.assignNearestShop(reqDto);
    }

    @GetMapping("/get-shop-id")
    public ResponseEntity<Long> getShopId(@RequestParam Long id) {
        return ResponseEntity.ok().body(shopService.getShopId(id));
    }

    /**
     * 주문 전체 조회용 가게 정보
     */
    @GetMapping("/shop-info-all")
    public ResponseEntity<AdminShopResDto> getShopInfo(@RequestParam Long shopId) {
        return ResponseEntity.ok().body(shopService.getShopInfo(shopId));
    }

    /**
     * 라이더 근처에 위치한 가게 조회
     */
    @GetMapping("/find-near-shop")
    public ResponseEntity<List<DeliveryAvailShopResDto>> getNearShop(@RequestParam Double latitude, @RequestParam Double longitude) {
        return ResponseEntity.ok().body(shopAssignService.findNearShop(latitude, longitude, 10));
    }

    /**
     * shopId로 가게 정보 조회 (리스트)
     */
    @GetMapping("/find-shopInfo")
    public ResponseEntity<List<DeliveryAvailShopResDto>> getShopInfoByIds(@RequestParam List<Long> ids) {
        return ResponseEntity.ok().body(shopService.getShopInfoList(ids));
    }

}
