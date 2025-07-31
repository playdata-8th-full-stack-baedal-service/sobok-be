package com.sobok.paymentservice.payment.dto.payment;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientNameResDto {
    private Long ingreId;
    private String ingreName;
}