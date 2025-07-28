package com.sobok.shopservice.shop.dto.stock;

import com.sobok.shopservice.shop.entity.Stock;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockResDto {
    private Long id;
    private Long shopId;
    private Long ingredientId;
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
