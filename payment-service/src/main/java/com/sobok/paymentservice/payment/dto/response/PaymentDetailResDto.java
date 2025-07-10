package com.sobok.paymentservice.payment.dto.response;

import com.sobok.paymentservice.common.enums.OrderState;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDetailResDto {
    private Long paymentId;
    private String orderId;
    private Long totalPrice;
    private OrderState orderState;
    private Long createdAt;
    private String payMethod;
    private String riderRequest;

    private List<PaymentItemResDto> items;

    private String roadFull;
    private String addrDetail;

    private String shopName;
    private String shopAddress;

    // 배달 완료된 시간
    private LocalDateTime completeTime;
}
