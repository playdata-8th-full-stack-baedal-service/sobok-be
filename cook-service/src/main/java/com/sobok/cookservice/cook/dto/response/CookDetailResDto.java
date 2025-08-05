package com.sobok.cookservice.cook.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "요리 상세 정보 DTO")
public class CookDetailResDto {

    @Schema(description = "요리 ID", example = "1")
    private Long cookId;

    @Schema(description = "요리 이름", example = "된장찌개")
    private String name;

    @Schema(description = "썸네일 이미지 URL", example = "http://example.com/image.jpg")
    private String thumbnail;

    @Schema(description = "활성화 상태", example = "Y")
    private String active;

    @Schema(description = "식재료 ID 목록")
    private List<Long> ingredientIds; // 식재료 id
}
