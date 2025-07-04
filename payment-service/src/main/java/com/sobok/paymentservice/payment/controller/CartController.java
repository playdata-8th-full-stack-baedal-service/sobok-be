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
        Long cartCookId = cartService.addCartCook(reqDto);
        return ResponseEntity.ok().body(ApiResponse.ok(cartCookId, "장바구니에 성공적으로 저장되었습니다."));
    }

    @PatchMapping("/edit-count")
    public ResponseEntity<?> editCount(@RequestParam Long id, @RequestParam Long count) {
        Long cartCookId = cartService.editCartCookCount(id, count);
        return ResponseEntity.ok().body(ApiResponse.ok(cartCookId, "장바구니 수량이 성공적으로 변경되었습니다."));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCartCook(@PathVariable Long id) {
        Long cartCookId = cartService.deleteCart(id);
        return ResponseEntity.ok().body(ApiResponse.ok(cartCookId, "장바구니의 상품이 성공적으로 삭제되었습니다."));
    }
    

}
