package com.sobok.cookservice.cook.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 기본 식재료 응답
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Schema(description = "기본 식재료 응답 DTO")
public class CookIngredientResDto {

    @Schema(description = "재료 Id", example = "1")
    private Long ingredientId;

    @Schema(description = "식재료 이름", example = "감자")
    private String ingreName;

    @Schema(description = "단위", example = "1")
    private Integer unit;

    @Schema(description = "가격", example = "1000")
    private Integer price;

    @Schema(description = "원산지", example = "국내산")
    private String origin;
}
