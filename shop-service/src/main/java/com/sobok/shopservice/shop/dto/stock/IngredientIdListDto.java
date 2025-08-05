package com.sobok.shopservice.shop.dto.stock;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "장바구니 재료 재고 리스트 DTO")
public class IngredientIdListDto {
    @Schema(
        description = "장바구니 재료 수량 목록",
        implementation = CartIngredientStock.class
    )
    private List<CartIngredientStock> cartIngredientStockList;
}
