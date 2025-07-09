package com.sobok.adminservice.admin.dto.order;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
// 요리 이름 식재료들(주문 조회용)
public class CookDetailWithIngredientsResDto {
    private String cookName;
    private List<String> baseIngredients;
    private List<String> additionalIngredients;
}