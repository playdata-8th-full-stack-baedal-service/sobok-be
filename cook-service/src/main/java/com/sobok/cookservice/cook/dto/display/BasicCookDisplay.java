package com.sobok.cookservice.cook.dto.display;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "요리 기본 정보 응답 DTO")
public class BasicCookDisplay {
    @Schema(description = "요리 ID", example = "123")
    private Long cookId;

    @Schema(description = "요리 이름", example = "김치찌개")
    private String cookName;

    @Schema(description = "썸네일 이미지 URL", example = "https://example.com/image.jpg")
    private String thumbnail;
}