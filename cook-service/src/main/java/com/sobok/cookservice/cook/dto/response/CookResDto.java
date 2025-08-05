package com.sobok.cookservice.cook.dto.response;

import com.sobok.cookservice.common.enums.CookCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 요리 기본 정보 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "요리 기본 정보 DTO")
public class CookResDto {

    @Schema(description = "요리 ID", example = "1")
    private Long id;

    @Schema(description = "요리 이름", example = "된장찌개")
    private String name;

    @Schema(description = "알레르기 정보", example = "콩, 우유")
    private String allergy;

    @Schema(description = "레시피 내용", example = "재료를 모두 넣고 끓인다.")
    private String recipe;

    @Schema(description = "카테고리", example = "SOUP")
    private CookCategory category;

    @Schema(description = "썸네일 URL", example = "http://example.com/image.jpg")
    private String thumbnail;

    @Schema(description = "활성화 상태", example = "Y")
    private String active;
}
