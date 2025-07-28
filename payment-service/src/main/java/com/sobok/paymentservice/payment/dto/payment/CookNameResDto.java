package com.sobok.paymentservice.payment.dto.payment;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 요리 이름 조회용(주문 조회)
public class CookNameResDto {
    private Long cookId;
    private String cookName;
}
