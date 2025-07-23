package com.sobok.cookservice.cook.dto.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
// 기본 식재료 응답
public class CookIngredientResDto {
    private Long ingredientId;      // 재료 Id
    private String ingreName;  // 식재료 이름
    private int unitQuantity;   // 몇 개
    private String unit;            // 단위
    private Integer price;
    private String origin;
}
