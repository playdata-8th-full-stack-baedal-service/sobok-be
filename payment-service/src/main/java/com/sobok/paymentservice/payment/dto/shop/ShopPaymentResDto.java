package com.sobok.paymentservice.payment.dto.shop;

import com.sobok.paymentservice.common.enums.OrderState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShopPaymentResDto {
    private Long paymentId;
    private String orderId;
    private OrderState orderState;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userAddressId;
}
