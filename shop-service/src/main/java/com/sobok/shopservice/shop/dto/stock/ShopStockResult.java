package com.sobok.shopservice.shop.dto.stock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopStockResult {
    private Long shopId;
    private Map<Long, Integer> stockMap;
    private boolean satisfiable;
    private List<MissingIngredientDto> missingIngredients;
}
