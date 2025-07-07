package com.sobok.paymentservice.payment.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 장바구니 조회 응답
public class PaymentResDto {
    private Long userId;
    private List<PaymentItemResDto> items;
}
