package com.sobok.shopservice.shop.dto.stock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AvailableShopInfoDto {
    private Long shopId;
    private String shopName;

    private List<CartIngredientStock> cartIngredientStockList;
}
