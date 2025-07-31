package com.sobok.paymentservice.payment.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyHot {
    Long cookId;
    Integer orderCount;
}