package com.sobok.shopservice.shop.controller;

import com.sobok.shopservice.common.dto.ApiResponse;
import com.sobok.shopservice.common.dto.TokenUserInfo;
import com.sobok.shopservice.shop.dto.stock.StockReqDto;
import com.sobok.shopservice.shop.dto.stock.StockResDto;
import com.sobok.shopservice.shop.service.ShopValidator;
import com.sobok.shopservice.shop.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
@Slf4j
public class StockController {
    private final StockService stockService;
    private final ShopValidator validator;

    /**
     * 식재료 재고 등록
     */
    @PostMapping()
    public ResponseEntity<?> registerStock(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @RequestBody @Valid StockReqDto reqDto
    ) {
        reqDto.setShopId(userInfo.getShopId());
        StockResDto result = stockService.registerStock(reqDto);
        return ApiResponse.response(result, "식재료 재고 등록이 정상적으로 처리되었습니다.");
    }

    /**
     * 식재료 재고 변경
     *
     * @param reqDto 반드시 변경 수량을 입력해주세요. (ex. 2개 증가 -> +2, 5개 감소 -> -5)
     */
    @PatchMapping()
    public ResponseEntity<?> deductStock(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @RequestBody @Valid StockReqDto reqDto
    ) {
        reqDto.setShopId(userInfo.getShopId());
        StockResDto result = stockService.deductStock(reqDto);
        return ApiResponse.response(result, "식재료 재고 사용이 정상적으로 처리되었습니다.");
    }

    /**
     * 식재료 재고 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getStock(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @PathVariable("id") Long shopId
    ) {
        validator.shopCheck(userInfo, shopId);
        List<StockResDto> result = stockService.getStock(shopId);
        return ApiResponse.response(result, "가게의 모든 식재료 재고 정보를 성공적으로 조회하였습니다.");
    }

    /**
     * 식재료 재고 조회 (가게용)
     */
    @GetMapping()
    public ResponseEntity<?> getStock(
            @AuthenticationPrincipal TokenUserInfo userInfo
    ) {
        Long shopId = userInfo.getShopId();
        List<StockResDto> result = stockService.getStock(shopId);
        return ApiResponse.response(result, "가게의 모든 식재료 재고 정보를 성공적으로 조회하였습니다.");
    }
}
