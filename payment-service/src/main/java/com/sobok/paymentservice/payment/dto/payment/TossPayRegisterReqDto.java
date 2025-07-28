package com.sobok.paymentservice.payment.dto.payment;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor
public class TossPayRegisterReqDto {
    private String orderId;
    private String paymentKey;
    private String method;
    private Integer totalPrice;
}
