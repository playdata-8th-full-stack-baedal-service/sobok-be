package com.sobok.cookservice.cook.dto.display;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DisplayParamDto {
    String category;
    String keyword;
    String sort;

    // Paging 관련 Param
    @NotNull
    Long pageNo;

    @NotNull @Max(50)
    Long numOfRows;
}
