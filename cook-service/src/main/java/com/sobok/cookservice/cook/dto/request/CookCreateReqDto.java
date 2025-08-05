package com.sobok.cookservice.cook.dto.request;

import com.sobok.cookservice.common.enums.CookCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "요리 생성 요청 DTO")
public class CookCreateReqDto {

    @Schema(description = "요리 이름", example = "된장찌개", required = true)
    @NotBlank(message = "요리 이름은 필수입니다.")
    private String name;

    @Schema(description = "알레르기 정보", example = "콩, 우유")
    private String allergy;

    @Schema(description = "요리 레시피", example = "재료를 모두 넣고 끓인다.", required = true)
    @NotBlank(message = "레시피는 필수입니다.")
    private String recipe;

    @Schema(description = "요리 카테고리", example = "KOREAN", required = true)
    @NotNull(message = "카테고리는 필수입니다.")
    private String category;

    @Schema(description = "썸네일 이미지 URL", example = "http://example.com/thumbnail.jpg", required = true)
    @NotBlank(message = "썸네일 URL은 필수입니다.")
    private String thumbnailUrl;

    @Schema(description = "식재료 목록", required = true)
    @NotEmpty(message = "식재료 목록은 최소 1개 이상이어야 합니다.")
    @Valid
    private List<IngredientDto> ingredients;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "요리 식재료 DTO")
    public static class IngredientDto {

        @Schema(description = "식재료 ID", example = "1", required = true)
        @NotNull(message = "식재료 ID는 필수입니다.")
        private Long ingredientId; // 식재료 id

        @Schema(description = "식재료 단위 수량", example = "3", required = true)
        @NotNull(message = "단위 수량은 필수입니다.")
        @Min(value = 1, message = "단위 수량은 1 이상이어야 합니다.")
        private Integer unitQuantity; // 식재료 단위
    }
}
