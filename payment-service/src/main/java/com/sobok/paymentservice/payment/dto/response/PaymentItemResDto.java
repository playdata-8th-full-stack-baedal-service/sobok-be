package com.sobok.paymentservice.payment.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 장바구니 조회
public class PaymentItemResDto {
    private Long cookId;
    private String cookName;
    private String thumbnail;
    private int quantity;
    private List<IngredientResDto> baseIngredients;       // 기본 식재료
    private List<IngredientResDto> additionalIngredients;  // 추가 식재료
}
