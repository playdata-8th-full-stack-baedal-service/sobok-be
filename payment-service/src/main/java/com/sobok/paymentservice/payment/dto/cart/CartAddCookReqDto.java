package com.sobok.paymentservice.payment.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartAddCookReqDto {
    private Long cookId;

    private List<AdditionalIngredient> additionalIngredients;

    private int count;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AdditionalIngredient {
        private Long ingreId;
        private int unitQuantity;
    }
}
