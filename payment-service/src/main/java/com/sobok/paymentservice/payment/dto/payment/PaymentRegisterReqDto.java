package com.sobok.paymentservice.payment.dto.payment;

import lombok.*;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
public class PaymentRegisterReqDto {
    private String orderId;
    private Long totalPrice;
    private String riderRequest;
    private Long userAddressId;
    private List<Long> cartCookIdList;
}
