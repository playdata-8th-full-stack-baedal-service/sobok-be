package com.sobok.shopservice.shop.dto.stock;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MissingIngredientDto {
    private Long ingredientId;
    private String ingredientName;
    private Integer quantity; // 가게가 보유한 수량
}
