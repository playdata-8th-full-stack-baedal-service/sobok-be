package com.sobok.paymentservice.payment.dto.payment;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockReqDto {
    @NotNull
    private Long shopId;

    @NotNull
    private Long ingredientId;

    @NotNull
    private Integer quantity;
}
