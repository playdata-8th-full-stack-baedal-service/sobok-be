package com.sobok.cookservice.cook.dto.response;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class IngredientInfoResDto {
    private Long ingredientId;
    private String ingredientName;
    private String unit;
    private Integer price;
    private String origin;
}