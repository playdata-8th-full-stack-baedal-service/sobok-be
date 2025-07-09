package com.sobok.paymentservice.payment.dto.payment;


import com.sobok.paymentservice.common.enums.OrderState;
import lombok.*;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
/**
 * 주문 전체 조회(결제)
 */
public class AdminPaymentResDto {
    private Long id;
    private String orderId;
    private Long totalPrice;
    private String payMethod;
    private OrderState orderState;
    private Long createdAt;
    private Long userAddressId;
}