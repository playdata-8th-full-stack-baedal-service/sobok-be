package com.sobok.paymentservice.payment.controller.payment;

import com.sobok.paymentservice.payment.dto.payment.TossPayRegisterReqDto;
import com.sobok.paymentservice.payment.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
}
