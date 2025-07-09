package com.sobok.paymentservice.payment.dto.response;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartIngredientResDto {
    private Long ingreId;
    private String defaultIngre;
    private Integer unitQuantity;
}