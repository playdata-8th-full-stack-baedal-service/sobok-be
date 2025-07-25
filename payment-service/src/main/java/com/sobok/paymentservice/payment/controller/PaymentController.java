package com.sobok.paymentservice.payment.controller;

import com.sobok.paymentservice.common.dto.ApiResponse;
import com.sobok.paymentservice.common.dto.TokenUserInfo;
import com.sobok.paymentservice.common.enums.DeliveryState;
import com.sobok.paymentservice.payment.client.DeliveryFeignClient;
import com.sobok.paymentservice.payment.dto.cart.CartAddCookReqDto;
import com.sobok.paymentservice.payment.dto.cart.CartStartPayDto;
import com.sobok.paymentservice.payment.dto.response.GetPaymentResDto;
import com.sobok.paymentservice.payment.dto.payment.PaymentRegisterReqDto;
import com.sobok.paymentservice.payment.dto.response.PaymentDetailResDto;
import com.sobok.paymentservice.payment.dto.response.PaymentResDto;
import com.sobok.paymentservice.payment.service.CartService;
import com.sobok.paymentservice.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;
    private final CartService cartService;
    private final DeliveryFeignClient deliveryFeignClient;

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
    public ResponseEntity<?> editCount(@AuthenticationPrincipal TokenUserInfo userInfo, @RequestParam Long id, @RequestParam Integer count, @RequestBody CartStartPayDto reqDto) {
        // cartCookId
        Long cartCookId = cartService.editCartCookCount(userInfo, id, count, reqDto);
        return ResponseEntity.ok().body(ApiResponse.ok(cartCookId, "장바구니 수량이 성공적으로 변경되었습니다."));
    }

    @DeleteMapping("/delete-cart/{id}")
    public ResponseEntity<?> deleteCartCook(@AuthenticationPrincipal TokenUserInfo userInfo, @PathVariable Long id, @RequestBody CartStartPayDto reqDto) {
        Long cartCookId = cartService.deleteCart(userInfo, id, reqDto);
        return ResponseEntity.ok().body(ApiResponse.ok(cartCookId, "장바구니의 상품이 성공적으로 삭제되었습니다."));
    }

    @PostMapping("/start-pay")
    public ResponseEntity<?> startPay(@AuthenticationPrincipal TokenUserInfo userInfo, @RequestBody CartStartPayDto reqDto) {
        cartService.startPay(userInfo, reqDto);
        return ResponseEntity.ok().body(ApiResponse.ok(userInfo.getUserId(), "성공적으로 장바구니의 정보가 저장되었습니다."));
    }

    @DeleteMapping("/fail-payment")
    public ResponseEntity<?> cancelPayment(@RequestParam String orderId) {
        String result = paymentService.resetPayment(orderId);
        return ResponseEntity.ok().body(ApiResponse.ok(result, "성공적으로 결제가 취소되었습니다."));
    }

    @DeleteMapping("/delete-payment")
    public void deletePayment(@RequestParam String orderId) {
        paymentService.cancelPayment(orderId);
    }

    /**
     * 사용자 주문 전체 조회
     */
    @GetMapping("/get-myPayment")
    public ResponseEntity<?> getPayment(@AuthenticationPrincipal TokenUserInfo userInfo,
                                        @RequestParam Long pageNo, @RequestParam Long numOfRows) {
        List<GetPaymentResDto> getPaymentResDtos = paymentService.getPayment(userInfo, pageNo, numOfRows);
        if(getPaymentResDtos.isEmpty()) {
            return ResponseEntity.ok().body(ApiResponse.ok(null, HttpStatus.NO_CONTENT));
        }
        return ResponseEntity.ok().body(ApiResponse.ok(getPaymentResDtos, "사용자의 주문 내역이 조회되었습니다."));
    }

    /**
     * 사용자 주문 세부 조회
     */
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getPaymentDetail(@AuthenticationPrincipal TokenUserInfo userInfo, @PathVariable("id") Long paymentId) {
        PaymentDetailResDto paymentDetail = paymentService.getPaymentDetail(userInfo, paymentId);
        return ResponseEntity.ok().body(ApiResponse.ok(paymentDetail, "주문 상세 내역이 조회되었습니다."));
    }

    /**
     * 주문 상태 변경
     */
    @PatchMapping("/change-orderState")
    public ResponseEntity<?> changeOrderState(@AuthenticationPrincipal TokenUserInfo userInfo, @RequestParam Long id) {
        paymentService.checkUserInfo(userInfo, id);
        return ResponseEntity.ok().body(ApiResponse.ok(id, "주문 상태가 변경되었습니다."));
    }

    /**
     * 라이더용 배달 승인
     */
    @PatchMapping("/accept-delivery")
    public ResponseEntity<?> acceptDelivery(@AuthenticationPrincipal TokenUserInfo userInfo, @RequestParam Long id) {
        paymentService.processDeliveryAction(userInfo, id, DeliveryState.ASSIGN, deliveryFeignClient::assignRider);
        return ResponseEntity.ok().body(ApiResponse.ok(id, "배달이 승인되었습니다."));
    }

    /**
     * 라이더용 배달 완료
     */
    @PatchMapping("/complete-delivery")
    public ResponseEntity<?> completeDelivery(@AuthenticationPrincipal TokenUserInfo userInfo, @RequestParam Long id) {
        paymentService.processDeliveryAction(userInfo, id, DeliveryState.COMPLETE, deliveryFeignClient::completeDelivery);
        return ResponseEntity.ok().body(ApiResponse.ok(id, "배달이 완료되었습니다."));
    }
}
