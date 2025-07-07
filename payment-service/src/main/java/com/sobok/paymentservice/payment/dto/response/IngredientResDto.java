package com.sobok.paymentservice.payment.dto.response;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 장바구니에 담긴 식재료
public class IngredientResDto {

    private Long ingredientId;
    private String ingreName;
    private int unitQuantity;
    private String unit;
}
