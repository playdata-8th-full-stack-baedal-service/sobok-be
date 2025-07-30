package com.sobok.paymentservice.payment.dto.response;

import com.sobok.paymentservice.common.enums.OrderState;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetPaymentResDto {
    private Long paymentId;
    private String orderId;
    private Long totalPrice;
    private OrderState orderState;
    private LocalDateTime createdAt;
    private List<Cook> cook;

    @Data
    @Builder
    public static class Cook {
        private Long cookId;
        private String cookName;
        private String thumbnail;
    }
}
