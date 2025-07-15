package com.sobok.cookservice.cook.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * 한달 주문량 기준 요리 페이지 조회
 */
public class PopularCookResDto {
    private Long cookId;
    private String cookName;
    private String thumbnail;
    private Long orderCount;
}