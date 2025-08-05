package com.sobok.cookservice.cook.dto.response;

import com.sobok.cookservice.cook.entity.Cook;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 한달 주문량 기준 인기 요리 DTO
 */
@Data
@Schema(description = "한달 주문량 기준 인기 요리 DTO")
public class MonthlyHotCookDto {

    @Schema(description = "요리 ID", example = "123")
    private Long cookId;

    @Schema(description = "요리 이름", example = "김치찌개")
    private String cookName;

    @Schema(description = "썸네일 이미지 URL", example = "https://example.com/image.jpg")
    private String thumbnail;

    @Schema(description = "한달 주문량", example = "100")
    private Integer orderCount;

    public MonthlyHotCookDto(Cook cook, CartMonthlyHotDto.MonthlyHot cartDto) {
        this.cookId = cook.getId();
        this.cookName = cook.getName();
        this.thumbnail = cook.getThumbnail();
        this.orderCount = cartDto.getOrderCount();
    }
}
