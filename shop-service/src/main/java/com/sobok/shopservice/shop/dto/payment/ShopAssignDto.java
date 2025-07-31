package com.sobok.shopservice.shop.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;

@Getter @ToString
@NoArgsConstructor @AllArgsConstructor
public class ShopAssignDto {
    Long userAddressId;
    Long paymentId;
    Map<Long, Integer> cartIngreIdList;
}
