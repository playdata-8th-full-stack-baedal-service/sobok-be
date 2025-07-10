package com.sobok.paymentservice.payment.dto.response;

import com.sobok.paymentservice.common.enums.OrderState;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PaymentDetailResDto {
    private Long paymentId;
    private String orderId;
    private Long totalPrice;
    private OrderState orderState;
    private Long createdAt;
    private String payMethod;
    private String riderRequest;

    private List<GetPaymentResDto.Cook> cook;

    private String address;

    @Data
    @Builder
    public static class Cook {
        private String cookName;
        private String thumbnail;
        private Long price;
    }
}
