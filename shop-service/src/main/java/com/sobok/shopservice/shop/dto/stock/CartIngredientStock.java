package com.sobok.shopservice.shop.dto.stock;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "장바구니 재료 수량 정보 DTO")
public class CartIngredientStock {
    @Schema(description = "가게 ID", example = "null")
    private Long shopId;
    @Schema(description = "재료 ID", example = "101")
    private Long ingredientId;
    @Schema(description = "해당 재료의 주문 수량", example = "5")
    private Integer quantity;

    public static Map<Long, Integer> convertIngreIdList(IngredientIdListDto reqDto) {
        return reqDto.getCartIngredientStockList()
                .stream()
                .collect(
                        Collectors.groupingBy(
                                CartIngredientStock::getIngredientId,
                                Collectors.summingInt(CartIngredientStock::getQuantity)
                        )
                );
    }
}
