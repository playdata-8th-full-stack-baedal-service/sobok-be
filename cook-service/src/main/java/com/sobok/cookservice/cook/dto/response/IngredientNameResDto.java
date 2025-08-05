package com.sobok.cookservice.cook.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 식재료 이름 응답 DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "식재료 이름 DTO")
public class IngredientNameResDto {

    @Schema(description = "식재료 ID", example = "1")
    private Long ingreId;

    @Schema(description = "식재료 이름", example = "감자")
    private String ingreName;
}
