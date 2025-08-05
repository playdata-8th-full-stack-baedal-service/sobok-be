package com.sobok.cookservice.cook.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 식재료 정보 응답 DTO
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "식재료 기본 정보 DTO")
public class IngredientInfoResDto {

    @Schema(description = "식재료 ID", example = "1")
    private Long ingredientId;

    @Schema(description = "식재료 이름", example = "감자")
    private String ingredientName;

    @Schema(description = "단위", example = "1")
    private Integer unit;

    @Schema(description = "가격", example = "1000")
    private Integer price;

    @Schema(description = "원산지", example = "국내산")
    private String origin;
}
