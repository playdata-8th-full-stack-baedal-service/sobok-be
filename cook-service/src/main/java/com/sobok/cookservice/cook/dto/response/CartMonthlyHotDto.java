package com.sobok.cookservice.cook.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@Schema(description = "한 달 주문량 기준 인기 요리 응답 DTO")
public class CartMonthlyHotDto {

    @Schema(description = "인기 요리 리스트")
    List<MonthlyHot> monthlyHot;

    @Schema(description = "조회 가능 여부")
    boolean isAvailable;

    @Schema(description = "총 인기 요리 수")
    int count;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "한 달 인기 요리 상세 DTO")
    public static class MonthlyHot {
        @Schema(description = "요리 ID", example = "1")
        Long cookId;

        @Schema(description = "주문 수량", example = "150")
        Integer orderCount;
    }
}
