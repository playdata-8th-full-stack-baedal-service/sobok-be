package com.sobok.cookservice.cook.dto.display;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "요리 조회 DTO")
public class DisplayParamDto {
    @Schema(description = "카테고리", example = "한식", nullable = true)
    String category;

    @Schema(description = "검색 키워드", example = "매운", nullable = true)
    String keyword;

    @Schema(description = "정렬 기준", example = "orderCountDesc", nullable = true)
    String sort;

    // Paging 관련 Param
    @NotNull
    @Schema(description = "페이지 번호", example = "1", required = true)
    Long pageNo;

    @NotNull @Max(50)
    @Schema(description = "페이지당 항목 수 (최대 50)", example = "10", required = true)
    Long numOfRows;
}
