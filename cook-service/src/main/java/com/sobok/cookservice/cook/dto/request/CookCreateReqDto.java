package com.sobok.cookservice.cook.dto.request;

import com.sobok.cookservice.common.enums.CookCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CookCreateReqDto {

    @NotBlank(message = "요리 이름은 필수입니다.")
    private String name;

    private String allergy;

    @NotBlank(message = "레시피는 필수입니다.")
    private String recipe;

    @NotNull(message = "카테고리는 필수입니다.")
    private String category;

    @NotBlank(message = "썸네일 URL은 필수입니다.")
    private String thumbnailUrl;

    @NotEmpty(message = "식재료 목록은 최소 1개 이상이어야 합니다.")
    @Valid
    private List<IngredientDto> ingredients;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IngredientDto {

        @NotNull(message = "식재료 ID는 필수입니다.")
        private Long ingredientId; // 식재료 id

        @NotNull(message = "단위 수량은 필수입니다.")
        @Min(value = 1, message = "단위 수량은 1 이상이어야 합니다.")
        private Integer unitQuantity; // 식재료 단위
    }
}
