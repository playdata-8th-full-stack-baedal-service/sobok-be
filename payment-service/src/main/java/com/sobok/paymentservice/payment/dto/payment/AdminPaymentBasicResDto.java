package com.sobok.paymentservice.payment.dto.payment;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminPaymentBasicResDto {
    private Long paymentId;
    private String orderId;
    private LocalDateTime createdAt;
}
