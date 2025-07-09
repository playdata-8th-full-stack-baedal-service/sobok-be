package com.sobok.adminservice.admin.dto.order;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartIngredientResDto {
    private Long ingreId;
    private String defaultIngre;
    private Integer unitQuantity;
}