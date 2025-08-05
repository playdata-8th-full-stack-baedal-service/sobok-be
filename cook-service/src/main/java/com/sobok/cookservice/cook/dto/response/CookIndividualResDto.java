package com.sobok.cookservice.cook.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "요리 개별 상세 정보 DTO")
public class CookIndividualResDto {

    @Schema(description = "요리 ID", example = "1")
    private Long cookId;

    @Schema(description = "요리 이름", example = "된장찌개")
    private String cookName;

    @Schema(description = "알레르기 정보", example = "콩, 우유")
    private String allergy;

    @Schema(description = "요리 카테고리", example = "SOUP")
    private String category;

    @Schema(description = "레시피 내용", example = "재료를 모두 넣고 끓인다.")
    private String recipe;

    @Schema(description = "썸네일 이미지 URL", example = "http://example.com/thumb.jpg")
    private String thumbnail;

    @Schema(description = "식재료 리스트")
    private List<IngredientAll> ingredientList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(description = "요리 식재료 상세 DTO")
    public static class IngredientAll {
        @Schema(description = "식재료 ID", example = "1")
        private Long ingredientId;

        @Schema(description = "식재료 이름", example = "감자")
        private String ingredientName;

        @Schema(description = "가격", example = "1000")
        private int price;

        @Schema(description = "단위", example = "1")
        private int unit;

        @Schema(description = "단위 수량", example = "3")
        private int unitQuantity;
    }
}
