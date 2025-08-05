package com.sobok.cookservice.cook.dto.display;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "월간 인기 요리 통계 DTO")
public class MonthlyHot {

    @Schema(description = "요리 ID", example = "123")
    private Long cookId;

    @Schema(description = "월간 주문 수", example = "150")
    private Integer orderCount;
}