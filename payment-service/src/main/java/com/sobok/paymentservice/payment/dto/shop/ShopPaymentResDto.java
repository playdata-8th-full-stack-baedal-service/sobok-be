package com.sobok.paymentservice.payment.dto.shop;

import com.sobok.paymentservice.common.enums.OrderState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShopPaymentResDto {
    private Long paymentId;
    private String orderId;
    private OrderState orderState;
    private Long createdAt;
    private Long userAddressId;
}
