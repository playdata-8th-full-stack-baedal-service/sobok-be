package com.sobok.paymentservice.payment.controller.payment;

import com.sobok.paymentservice.common.dto.ApiResponse;
import com.sobok.paymentservice.payment.dto.payment.AdminPaymentResDto;
import com.sobok.paymentservice.payment.dto.payment.TossPayRegisterReqDto;
import com.sobok.paymentservice.payment.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class PaymentFeignController {

    private final PaymentService paymentService;

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
    public ApiResponse<List<AdminPaymentResDto>> getAllPayments() {
        List<AdminPaymentResDto> result = paymentService.getAllPaymentsForAdmin();
        return ApiResponse.ok(result, "전체 주문 조회 성공");
    }

    /**
     * 결제 정보에 맞는 요리 이름 조회용
     */
    @GetMapping("/admin/cook-ids")
    public ResponseEntity<List<Long>> getCookIdsByPaymentId(@RequestParam Long paymentId) {
        return ResponseEntity.ok(paymentService.getCookIdsByPaymentId(paymentId));
    }
}
