package com.sobok.shopservice.shop.dto.stock;

import com.sobok.shopservice.shop.entity.Stock;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "식재료 재고 등록 요청 DTO")
public class StockReqDto {
    @Schema(description = "가게 ID (자동으로 인증된 사용자 정보에서 설정됨)", example = "10", accessMode = Schema.AccessMode.READ_ONLY)
    private Long shopId;

    @Schema(description = "재료 ID", example = "45")
    @NotNull(message = "ingredientId는 필수입니다.")
    private Long ingredientId;

    @Schema(description = "재고 수량 (0 이상이어야 함)", example = "50")
    @NotNull(message = "quantity는 필수입니다.")
    private Integer quantity;

    public Stock toEntity() {
        return Stock.builder()
                .shopId(shopId)
                .ingredientId(ingredientId)
                .quantity(quantity)
                .build();
    }
}
