package com.sobok.cookservice.cook.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 한달 주문량 기준 요리 페이지 조회 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "한달 주문량 기준 인기 요리 페이지 조회 DTO")
public class PopularCookResDto {

    @Schema(description = "요리 ID", example = "1")
    private Long cookId;

    @Schema(description = "요리 이름", example = "된장찌개")
    private String cookName;

    @Schema(description = "썸네일 URL", example = "http://example.com/image.jpg")
    private String thumbnail;

    @Schema(description = "주문 수량", example = "1000")
    private Long orderCount;
}
