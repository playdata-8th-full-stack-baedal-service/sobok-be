package com.sobok.shopservice.shop.dto.stock;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "사용자가 선택한 재료로 주문 가능한 가게 정보 DTO")
public class AvailableShopInfoDto {
    @Schema(description = "가게 ID", example = "1")
    private Long shopId;

    @Schema(description = "가게 이름", example = "강남 본점")
    private String shopName;

    @Schema(description = "장바구니에 담긴 재료 정보 목록")
    private List<CartIngredientStock> cartIngredientStockList;

    @Schema(description = "해당 가게에서 주문이 가능한지 여부", example = "true")
    private boolean satisfiable;

    @Schema(description = "해당 가게에서 빠진 재료 목록 (주문 불가한 경우)", nullable = true)
    private List<MissingIngredientDto> missingIngredients;
}
