package com.sobok.paymentservice.payment.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CartStartPayDto {
    private int totalPrice;
    private List<Long> selectedItems;
}
