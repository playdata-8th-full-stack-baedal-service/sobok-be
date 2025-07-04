package com.sobok.paymentservice.payment.controller;

import com.sobok.paymentservice.common.dto.ApiResponse;
import com.sobok.paymentservice.payment.dto.cart.CartAddCookReqDto;
import com.sobok.paymentservice.payment.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {
    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<?> addCartCook(@RequestBody CartAddCookReqDto reqDto) {
        cartService.addCartCook(reqDto);
        return ResponseEntity.ok().body(ApiResponse.ok(reqDto.getUserId(), "장바구니에 성공적으로 저장되었습니다."));
    }

    

}
