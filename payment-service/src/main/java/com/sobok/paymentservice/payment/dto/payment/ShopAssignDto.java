package com.sobok.paymentservice.payment.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter @ToString
@NoArgsConstructor @AllArgsConstructor
public class ShopAssignDto {
    Long userAddressId;
    Long paymentId;
}
