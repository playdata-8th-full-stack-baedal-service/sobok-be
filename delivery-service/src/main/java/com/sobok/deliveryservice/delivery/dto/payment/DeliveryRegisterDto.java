package com.sobok.deliveryservice.delivery.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter @ToString
@AllArgsConstructor
public class DeliveryRegisterDto {
    private Long shopId;
    private Long paymentId;
}
