package com.sobok.cookservice.cook.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
public class CartMonthlyHotDto {
    List<MonthlyHot> monthlyHot;
    boolean isAvailable;
    int count;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MonthlyHot {
        Long cookId;
        Integer orderCount;
    }
}
