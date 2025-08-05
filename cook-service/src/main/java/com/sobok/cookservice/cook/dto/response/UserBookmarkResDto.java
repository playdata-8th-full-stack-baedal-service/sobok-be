package com.sobok.cookservice.cook.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 사용자 북마크 응답 DTO
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사용자 북마크 요리 DTO")
public class UserBookmarkResDto {

    @Schema(description = "요리 ID", example = "1")
    private Long cookId;

    @Schema(description = "요리 이름", example = "김치찌개")
    private String cookName;

    @Schema(description = "썸네일 URL", example = "http://example.com/image.jpg")
    private String thumbnail;
}
