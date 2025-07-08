package com.sobok.adminservice.admin.dto.order;

import com.sobok.adminservice.common.enums.OrderState;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
/**
 * 주문 조회 응답용(필요한것만 AdminPaymentResDto 에서 걸름)
 */
public class AdminPaymentResponseDto {
    private String orderId;
    private Long totalPrice;
    private String payMethod;
    private OrderState orderState;
    private Long createdAt;

    private String nickname;
    private String roadFull;
    private String address;
    private String riderName;
}
