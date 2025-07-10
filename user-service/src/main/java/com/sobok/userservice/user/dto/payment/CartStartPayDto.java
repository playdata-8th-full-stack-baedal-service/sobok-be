package com.sobok.userservice.user.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CartStartPayDto {
    private int totalPrice;
    private List<Long> selectedItems;
}
