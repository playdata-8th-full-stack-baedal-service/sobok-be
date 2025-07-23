package com.sobok.cookservice.cook.dto.response;

import com.sobok.cookservice.cook.entity.Cook;
import lombok.Data;

@Data
public class MonthlyHotCookDto {
    private Long cookId;
    private String cookName;
    private String thumbnail;
    private Integer orderCount;

    public MonthlyHotCookDto(Cook cook, CartMonthlyHotDto.MonthlyHot cartDto) {
        this.cookId = cook.getId();
        this.cookName = cook.getName();
        this.thumbnail = cook.getThumbnail();
        this.orderCount = cartDto.getOrderCount();
    }
}
