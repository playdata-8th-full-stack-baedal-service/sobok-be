package com.sobok.paymentservice.payment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "장바구니 내 요리 정보 DTO")
// 장바구니 조회
public class PaymentItemResDto {
   @Schema(description = "장바구니-요리 ID", example = "17")
    private Long id;

    @Schema(description = "요리 ID", example = "5")
    private Long cookId;

    @Schema(description = "요리 이름", example = "김치찌개")
    private String cookName;

    @Schema(description = "요리 썸네일 이미지 url", example = "https://d3c5012dwkvoyc.cloudfront.net/food/cbab6af8-250d-4efc-bd8c-f1d74588605fpexels-catscoming-955137.jpg")
    private String thumbnail;

    @Schema(description = "활성 상태 (예: 'Y' 또는 'N')", example = "Y")
    private String active;

    @Schema(description = "주문 수량", example = "2")
    private int quantity;

    @Schema(description = "기본 식재료 리스트")
    private List<IngredientResDto> baseIngredients;       // 기본 식재료

    @Schema(description = "추가 식재료 리스트")
    private List<IngredientResDto> additionalIngredients;  // 추가 식재료

    @Schema(description = "결제 ID", example = "101")
    private Long paymentId;
}
