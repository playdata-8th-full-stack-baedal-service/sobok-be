package com.sobok.adminservice.admin.dto.order;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartCookResDto {
    private Long id;          // cartCookId
    private Long cookId;
    private String cookName;
    private Integer quantity;
}