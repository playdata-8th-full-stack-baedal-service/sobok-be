package com.sobok.cookservice.cook.dto.response;


import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class CookWithIngredientResDto {

    private Long ingredientId;
    private String ingredientName;
    private Integer unit;
    private int quantity;
    private int price;
    private String origin;
    private boolean isDefault;
}
