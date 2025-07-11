package com.sobok.cookservice.cook.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CookIndividualResDto {
    private Long cookId;
    private String cookName;
    private String allergy;
    private String category;
    private String recipe;
    private String thumbnail;
    private List<IngredientAll> ingredientList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class IngredientAll {
        private Long ingredientId;
        private String ingredientName;
        private int price;
        private int unit;
        private int unitQuantity;
    }
}
