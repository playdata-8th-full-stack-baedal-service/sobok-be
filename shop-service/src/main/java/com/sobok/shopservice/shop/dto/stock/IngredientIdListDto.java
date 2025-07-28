package com.sobok.shopservice.shop.dto.stock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngredientIdListDto {
    private List<CartIngredientStock> cartIngredientStockList;
}
