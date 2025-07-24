package com.sobok.cookservice.cook.dto.display;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyHot {
    Long cookId;
    Integer orderCount;
}