package com.sobok.postservice.post.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientResDto {

    private Long ingredientId;
    private String ingredientName;
    private Integer unit;
    private int quantity;
    private int price;
    private String origin;
    @JsonIgnore
    private boolean defaultFlag;
}