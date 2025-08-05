package com.sobok.shopservice.shop.dto.stock;

import com.sobok.shopservice.shop.entity.Stock;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "식재료 재고 등록/조회 응답 DTO")
public class StockResDto {
    @Schema(description = "재고 ID", example = "120")
    private Long id;
    @Schema(description = "가게 ID", example = "10")
    private Long shopId;
    @Schema(description = "재료 ID", example = "45")
    private Long ingredientId;
    @Schema(description = "재고 수량", example = "50")
    private Integer quantity;

    public static StockResDto toDto(Stock stock) {
        return new StockResDto(
                stock.getId(),
                stock.getShopId(),
                stock.getIngredientId(),
                stock.getQuantity()
        );
    }
}
