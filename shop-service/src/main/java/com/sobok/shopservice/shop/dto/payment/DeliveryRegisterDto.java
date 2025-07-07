package com.sobok.shopservice.shop.dto.payment;

import lombok.*;

@Getter @ToString
@AllArgsConstructor
public class DeliveryRegisterDto {
    private Long shopId;
    private Long paymentId;
}
