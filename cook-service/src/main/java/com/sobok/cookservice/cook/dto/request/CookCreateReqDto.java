package com.sobok.cookservice.cook.dto.request;

import com.sobok.cookservice.common.enums.CookCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CookCreateReqDto {

    private String name;
    private String allergy;
    private String recipe;
    private CookCategory category;
    private String thumbnailUrl;

    private List<IngredientDto> ingredients;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IngredientDto {
        private Long ingredientId; // 식재료 id
        private Integer unitQuantity; // 식재료 단위
    }
}
