package com.sobok.paymentservice.payment.dto.response;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartCookResDto {
    private Long id;         // cartCookId
    private Long cookId;
    private String cookName;
    private Integer quantity;
}