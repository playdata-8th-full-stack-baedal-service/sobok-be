package com.sobok.deliveryservice.delivery.dto.payment;

import com.sobok.deliveryservice.common.enums.OrderState;
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
