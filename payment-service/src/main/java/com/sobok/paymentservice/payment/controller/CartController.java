package com.sobok.paymentservice.payment.controller;

import com.sobok.paymentservice.common.dto.ApiResponse;
import com.sobok.paymentservice.common.dto.TokenUserInfo;
import com.sobok.paymentservice.payment.dto.cart.DeleteCartReqDto;
import com.sobok.paymentservice.payment.service.CartService;
import com.sobok.paymentservice.payment.service.cart.CartCookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@Slf4j
@RequiredArgsConstructor
public class CartController {
    private final CartCookService cartService;

    @PatchMapping("/{id}")
    public ResponseEntity<?> editCartCookCount(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @PathVariable(name = "id") Long cartCookId,
            @RequestParam Integer count
    ) {
        Long result = cartService.editCartCook(userInfo, cartCookId, count);
        return ApiResponse.response(result, "장바구니 요리 수량 변경이 정상적으로 처리되었습니다.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCartCook(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @PathVariable(name = "id") Long cartCookId
    ) {
        Long result = cartService.deleteCartCook(userInfo, cartCookId);
        return ApiResponse.response(result, "장바구니 요리 삭제가 정상적으로 처리되었습니다.");
    }

    @DeleteMapping("/all")
    public ResponseEntity<?> deleteCart(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @RequestBody DeleteCartReqDto reqDto
    ) {
        cartService.deleteCartCookList(userInfo, reqDto);
        return ApiResponse.response(userInfo.getUserId(), "장바구니의 모든 요리 제거가 성공적으로 처리되었습니다.");
    }

}
