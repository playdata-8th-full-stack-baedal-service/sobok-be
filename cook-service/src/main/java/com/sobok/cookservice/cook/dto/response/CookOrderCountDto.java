package com.sobok.cookservice.cook.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 한달 주문량 기준 요리 페이지 조회
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "한달 주문량 기준 요리 조회 DTO")
public class CookOrderCountDto {

    @Schema(description = "요리 ID", example = "1")
    private Long cookId;

    @Schema(description = "주문 수량", example = "150")
    private Long orderCount;
}
