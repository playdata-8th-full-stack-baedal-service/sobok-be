package com.sobok.paymentservice.payment.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 요리 정보
public class CookDetailResDto {
    private Long cookId;
    private String name;
    private String thumbnail;
    private String active;
    private List<IngredientResDto> ingredients;
}
