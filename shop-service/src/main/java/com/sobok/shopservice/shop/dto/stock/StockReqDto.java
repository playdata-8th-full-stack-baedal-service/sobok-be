package com.sobok.shopservice.shop.dto.stock;

import com.sobok.shopservice.shop.entity.Stock;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockReqDto {
    private Long shopId;

    @NotNull
    private Long ingredientId;

    @NotNull
    private Integer quantity;

    public Stock toEntity() {
        return Stock.builder()
                .shopId(shopId)
                .ingredientId(ingredientId)
                .quantity(quantity)
                .build();
    }
}
