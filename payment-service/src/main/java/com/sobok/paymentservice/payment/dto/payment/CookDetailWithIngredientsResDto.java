package com.sobok.paymentservice.payment.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "요리 상세 정보 및 재료")
// 요리 이름 식재료들(주문 조회용)
public class CookDetailWithIngredientsResDto {

    @Schema(description = "요리 이름", example = "오이샐러드")
    private String cookName;

    @Schema(description = "기본 식재료 리스트", example = "[\"계란\", \"양파\"]")
    private List<String> baseIngredients;

    @Schema(description = "추가 식재료 리스트", example = "[\"비엔나소세지\"]")
    private List<String> additionalIngredients;
}