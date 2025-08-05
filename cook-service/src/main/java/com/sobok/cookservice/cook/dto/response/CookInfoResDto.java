package com.sobok.cookservice.cook.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "요리 기본 정보 DTO")
public class CookInfoResDto {

    @Schema(description = "요리 ID", example = "1")
    private Long cookId;

    @Schema(description = "요리 이름", example = "된장찌개")
    private String name;

    @Schema(description = "썸네일 이미지 URL", example = "http://example.com/thumb.jpg")
    private String thumbnail;

    @Schema(description = "활성화 상태", example = "Y")
    private String active;
}
