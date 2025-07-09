package com.sobok.paymentservice.payment.controller;

import com.sobok.paymentservice.common.dto.ApiResponse;
import com.sobok.paymentservice.common.dto.TokenUserInfo;
import com.sobok.paymentservice.payment.dto.cart.CartAddCookReqDto;
import com.sobok.paymentservice.payment.dto.payment.PaymentRegisterReqDto;
import com.sobok.paymentservice.payment.dto.response.PaymentResDto;
import com.sobok.paymentservice.payment.service.CartService;
import com.sobok.paymentservice.payment.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;
    private final CartService cartService;

    @PostMapping("/register")
    public ResponseEntity<?> registerPayment(@RequestBody PaymentRegisterReqDto reqDto) {
        Long paymentId = paymentService.registerPayment(reqDto);
        return ResponseEntity.ok().body(ApiResponse.ok(paymentId, "주문 사전 정보가 정상적으로 저장되었습니다."));
    }

    /**
     * 기존의 CartController
     **/
    @PostMapping("/add-cart")
    public ResponseEntity<?> addCartCook(@AuthenticationPrincipal TokenUserInfo userInfo, @RequestBody CartAddCookReqDto reqDto) {
        Long cartCookId = cartService.addCartCook(userInfo, reqDto);
        return ResponseEntity.ok().body(ApiResponse.ok(cartCookId, "장바구니에 성공적으로 저장되었습니다."));
    }

    /**
     * 장바구니 조회용
     */
    @GetMapping("/get-cart")
    public ResponseEntity<?> getCart(@AuthenticationPrincipal TokenUserInfo userInfo) {
        PaymentResDto resDto = cartService.getCart(userInfo);
        return ResponseEntity.ok(ApiResponse.ok(resDto, "장바구니 조회 성공"));
    }

    @PatchMapping("/cart-count-edit")
    public ResponseEntity<?> editCount(@RequestParam Long id, @RequestParam Integer count) {
        // cartCookId
        Long cartCookId = cartService.editCartCookCount(id, count);
        return ResponseEntity.ok().body(ApiResponse.ok(cartCookId, "장바구니 수량이 성공적으로 변경되었습니다."));
    }

    @DeleteMapping("/delete-cart/{id}")
    public ResponseEntity<?> deleteCartCook(@PathVariable Long id) {
        Long cartCookId = cartService.deleteCart(id);
        return ResponseEntity.ok().body(ApiResponse.ok(cartCookId, "장바구니의 상품이 성공적으로 삭제되었습니다."));
    }

}
