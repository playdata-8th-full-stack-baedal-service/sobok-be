package com.sobok.cookservice.cook.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 요리 + 식재료 상세 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Schema(description = "요리 식재료 상세 DTO")
public class CookWithIngredientResDto {

    @Schema(description = "식재료 ID", example = "1")
    private Long ingredientId;

    @Schema(description = "식재료 이름", example = "감자")
    private String ingredientName;

    @Schema(description = "단위", example = "1")
    private Integer unit;

    @Schema(description = "수량", example = "3")
    private int quantity;

    @Schema(description = "가격", example = "1000")
    private int price;

    @Schema(description = "원산지", example = "국내산")
    private String origin;

    @Schema(description = "기본 식재료 여부", example = "true")
    private boolean isDefault;
}
