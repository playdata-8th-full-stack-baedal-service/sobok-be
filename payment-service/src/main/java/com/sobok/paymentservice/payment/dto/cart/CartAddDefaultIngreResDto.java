package com.sobok.paymentservice.payment.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartAddDefaultIngreResDto {
    Map<Long, Integer> defaultIngreInfoList;
}
