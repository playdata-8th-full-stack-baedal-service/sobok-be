package com.sobok.paymentservice.payment.controller;

import com.sobok.paymentservice.common.dto.ApiResponse;
import com.sobok.paymentservice.common.dto.TokenUserInfo;
import com.sobok.paymentservice.common.enums.OrderState;
import com.sobok.paymentservice.common.exception.CustomException;
import com.sobok.paymentservice.payment.client.UserServiceClient;
import com.sobok.paymentservice.payment.dto.payment.*;
import com.sobok.paymentservice.payment.dto.response.CartCookResDto;
import com.sobok.paymentservice.payment.dto.response.CartIngredientResDto;
import com.sobok.paymentservice.payment.entity.Payment;
import com.sobok.paymentservice.payment.repository.PaymentRepository;
import com.sobok.paymentservice.payment.dto.shop.ShopPaymentResDto;
import com.sobok.paymentservice.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class PaymentFeignController {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final UserServiceClient userServiceClient;

    @PostMapping("/register-payment")
    public void registerPayment(@RequestBody TossPayRegisterReqDto reqDto) {
        paymentService.completePayment(reqDto);
    }

    @DeleteMapping("/delete-payment")
    public void cancelPayment(@RequestBody String orderId) {
        paymentService.cancelPayment(orderId);
    }

    /**
     * 관리자 전용 전체 주문 조회
     */
    @GetMapping("/admin/payments")
    public ApiResponse<PagedResponse<AdminPaymentResDto>> getAllPayments(@RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "10") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")); // 최신순 정렬
        PagedResponse<AdminPaymentResDto> result = paymentService.getAllPaymentsForAdmin(pageable);
        return ApiResponse.ok(result, "관리자 주문 페이징 조회 성공");
    }

    /**
     * 결제 정보에 맞는 요리 이름 조회용
     */
    @GetMapping("/admin/cook-ids")
    public ResponseEntity<List<Long>> getCookIdsByPaymentId(@RequestParam Long paymentId) {
        return ResponseEntity.ok(paymentService.getCookIdsByPaymentId(paymentId));
    }

    /**
     * 결제 ID에 해당하는 모든 장바구니 요리 목록을 조회
     */
    @GetMapping("/admin/cart-cooks")
    public List<CartCookResDto> getCartCooks(@RequestParam Long paymentId) {
        return paymentService.getCartCooksByPaymentId(paymentId);
    }

    /**
     * 특정 장바구니 요리에 포함된 재료 목록을 조회
     */
    @GetMapping("/admin/cart-ingredients")
    public List<CartIngredientResDto> getCartIngredients(@RequestParam Long cartCookId) {
        return paymentService.getIngredientsByCartCookId(cartCookId);
    }


    @GetMapping("/payment/completed")
    public Boolean isPaymentCompleted(@RequestParam Long paymentId, @RequestParam Long userId) {
        return paymentService.isPaymentCompleted(paymentId, userId);
    }


    /**
     * 결제 ID로 연결된 요리 중 하나의 cookId를 반환
     */
    @GetMapping("/payment/cook-id")
    public Long getCookIdByPaymentId(@RequestParam Long paymentId) {
        return paymentService.getCookIdsByPaymentId(paymentId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new CustomException("해당 주문에 요리가 없습니다.", HttpStatus.NOT_FOUND));
    }

    /**
     * 가게에 들어온 전체 주문 조회용 paymentId로 주문 정보 받기
     */
    @GetMapping("/getPayment")
    public List<ShopPaymentResDto> getPayment(@RequestParam List<Long> id) {
        return paymentService.getPaymentList(id);
    }

    /**
     * 배달 가능한 주문 목록 조회에 사용되는 paymentId로 주문 정보 받기
     */
    @GetMapping("/getRiderAvailPayment")
    public List<ShopPaymentResDto> getRiderAvailPayment(@RequestParam List<Long> id) {
        return paymentService.getRiderAvailPaymentList(id);
    }

    /**
     * 라이더용 배달 승인
     */
    @PatchMapping("/accept-delivery")
    public void acceptDelivery(@RequestBody RiderChangeOrderStateReqDto changeOrderState){
        paymentService.checkUserInfo(changeOrderState.getUserInfo(), changeOrderState.getPaymentId());
    }
}
