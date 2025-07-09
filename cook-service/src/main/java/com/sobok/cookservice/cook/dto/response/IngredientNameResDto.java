package com.sobok.cookservice.cook.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientNameResDto {
    private Long ingreId;
    private String ingreName;
}