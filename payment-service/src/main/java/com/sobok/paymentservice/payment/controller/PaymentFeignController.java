package com.sobok.paymentservice.payment.controller;

import com.sobok.paymentservice.common.dto.ApiResponse;
import com.sobok.paymentservice.common.enums.OrderState;
import com.sobok.paymentservice.common.exception.CustomException;
import com.sobok.paymentservice.payment.client.UserServiceClient;
import com.sobok.paymentservice.payment.dto.cart.CartMonthlyHotDto;
import com.sobok.paymentservice.payment.dto.payment.*;
import com.sobok.paymentservice.payment.dto.response.*;
import com.sobok.paymentservice.payment.repository.PaymentRepository;
import com.sobok.paymentservice.payment.dto.shop.ShopPaymentResDto;
import com.sobok.paymentservice.payment.service.CartService;
import com.sobok.paymentservice.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class PaymentFeignController {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final UserServiceClient userServiceClient;
    private final CartService cartService;

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
     * 결제 상태 정보
     */
    @GetMapping("/payment/completed")
    public ResponseEntity<?> isPaymentCompleted(@RequestParam Long paymentId, @RequestParam Long userId) {
        return ResponseEntity.ok().body(paymentService.isPaymentCompleted(paymentId, userId));
    }

    /**
     * 가게에 들어온 전체 주문 조회용 paymentId로 주문 정보 받기
     */
    @GetMapping("/getPayment")
    public ResponseEntity<List<ShopPaymentResDto>> getPayment(@RequestParam List<Long> id) {
        return ResponseEntity.ok().body(paymentService.getPaymentList(id));
    }

    /**
     * 배달 가능한 주문 목록 조회에 사용되는 paymentId로 주문 정보 받기
     */
    @GetMapping("/getRiderAvailPayment")
    public ResponseEntity<List<ShopPaymentResDto>> getRiderAvailPayment(@RequestParam List<Long> id) {
        List<ShopPaymentResDto> riderAvailPaymentList = paymentService.getRiderAvailPaymentList(id, List.of(OrderState.READY_FOR_DELIVERY));
        return ResponseEntity.ok().body(riderAvailPaymentList);
    }

    /**
     * 배달 목록 조회에 사용되는 paymentId로 주문 정보 받기
     */
    @GetMapping("/getRiderPayment")
    public ResponseEntity<List<ShopPaymentResDto>> getRiderPayment(@RequestParam List<Long> id) {
         return ResponseEntity.ok().body(paymentService.getRiderAvailPaymentList(id, null));
    }

    /**
     * paymentId로 cartCookId 조회
     */
    @GetMapping("/payment/cart-cook-id")
    public ResponseEntity<Long> getCartCookIdByPaymentId(@RequestParam Long paymentId) {
        return ResponseEntity.ok().body(paymentService.getCartCookIdByPaymentId(paymentId));
    }

    /**
     * cartCookId 기준으로 추가 식재료 목록 조회
     */
    @GetMapping("/payment/default-ingredients")
    public ResponseEntity<List<IngredientTwoResDto>> getDefaultIngredients(@RequestParam Long cookId) {
        return ResponseEntity.ok().body(paymentService.getDefaultIngredients(cookId));
    }

    /**
     * 요리 ID로 요리 이름 조회
     */
    @GetMapping("/payment/extra-ingredients")
    public ResponseEntity<List<IngredientTwoResDto>> getExtraIngredients(@RequestParam Long cartCookId) {
        return ResponseEntity.ok().body(paymentService.getExtraIngredients(cartCookId));
    }

    /**
     * 요리 ID(cookId)를 기반으로 요리 이름을 조회
     */
    @GetMapping("/payment/cook-name")
    public String getCookName(@RequestParam Long cookId) {
        return paymentService.getCookName(cookId);
    }


    /**
     * 한달 주문량 기준 요리 페이지 조회
     */
    @GetMapping("/popular-cook-ids")
    public ResponseEntity<?> getPopularCookIds(@RequestParam int page,
                                               @RequestParam int size) {
        return ResponseEntity.ok().body(cartService.getPopularCookIds(page, size));
    }

//    @GetMapping("/monthly-hot")
//    public CartMonthlyHotDto getMonthlyHotCooks(@RequestParam int pageNo, @RequestParam int numOfRows) {
//        return cartService.getMonthlyHotList(pageNo, numOfRows);
//    }
}
