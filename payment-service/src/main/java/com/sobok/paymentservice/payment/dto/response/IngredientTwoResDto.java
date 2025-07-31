package com.sobok.paymentservice.payment.dto.response;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 게시글 조회용
public class IngredientTwoResDto {

    private Long ingredientId;
    private String ingredientName;
    private Integer unit;
    private int quantity;
    private int price;
    private String origin;
    @JsonIgnore
    private boolean defaultFlag;

}
