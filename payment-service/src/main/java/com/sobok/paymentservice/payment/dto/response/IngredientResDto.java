package com.sobok.paymentservice.payment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Schema(description = "장바구니 내 식재료 정보 DTO")
// 장바구니에 담긴 식재료
public class IngredientResDto {

    @Schema(description = "식재료 ID", example = "10")
    private Long ingredientId;

    @Schema(description = "식재료 이름", example = "두부")
    private String ingreName;

    @Schema(description = "사용 수량", example = "5")
    private int unitQuantity;

    @Schema(description = "단위", example = "10")
    private Integer unit;

    @Schema(description = "가격", example = "5")
    private int price;

    @Schema(description = "원산지", example = "국내산")
    private String origin;
}
