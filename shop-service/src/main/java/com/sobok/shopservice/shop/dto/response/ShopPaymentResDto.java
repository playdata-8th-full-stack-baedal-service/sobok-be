package com.sobok.shopservice.shop.dto.response;

import com.sobok.shopservice.common.enums.OrderState;
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
//    private Long createdAt;
    private Long updatedAt;
}
