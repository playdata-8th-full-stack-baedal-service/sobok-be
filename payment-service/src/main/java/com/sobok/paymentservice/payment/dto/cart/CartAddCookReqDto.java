package com.sobok.paymentservice.payment.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "장바구니 등록 요청 DTO")
public class CartAddCookReqDto {
    @Schema(description = "요리 ID", example = "5")
    private Long cookId;

    @Schema(description = "추가 재료 리스트")
    private List<AdditionalIngredient> additionalIngredients;

    @Schema(description = "요리 수량 (최소 1)", example = "2")
    private int count;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AdditionalIngredient {

        @Schema(description = "재료 ID", example = "3")
        private Long ingreId;

        @Schema(description = "추가 수량", example = "2")
        private int unitQuantity;
    }
}
