package com.sobok.adminservice.admin.dto.order;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientNameResDto {
    private Long ingreId;
    private String ingreName;
}