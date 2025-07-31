package com.sobok.paymentservice.payment.dto.response;

import lombok.*;

@Getter
@AllArgsConstructor
@Setter
@NoArgsConstructor
@Builder
/**
 * 한달 주문량 기준 요리 페이지 조회
 */
public class CookOrderCountDto {
    private Long cookId;
    private Long orderCount;
}