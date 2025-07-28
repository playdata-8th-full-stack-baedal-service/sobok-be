package com.sobok.shopservice.shop.dto.stock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartIngredientStock {
    private Long shopId;
    private Long ingredientId;
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
