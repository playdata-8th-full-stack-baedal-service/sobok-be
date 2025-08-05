package com.sobok.cookservice.cook.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 요리 이름 전달용 (주문조회)
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "요리 이름 응답 DTO")
public class CookNameResDto {

    @Schema(description = "요리 ID", example = "1")
    private Long cookId;

    @Schema(description = "요리 이름", example = "된장찌개")
    private String cookName;
}
