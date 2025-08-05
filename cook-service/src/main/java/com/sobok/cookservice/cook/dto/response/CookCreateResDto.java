package com.sobok.cookservice.cook.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "요리 생성 응답 DTO")
public class CookCreateResDto {
    @Schema(description = "생성된 요리 ID", example = "1")
    private Long cookId;
}
