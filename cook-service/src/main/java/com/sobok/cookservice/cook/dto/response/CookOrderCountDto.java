package com.sobok.cookservice.cook.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * 한달 주문량 기준 요리 페이지 조회
 */
public class CookOrderCountDto {
    private Long cookId;
    private Long orderCount;
}