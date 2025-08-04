package com.sobok.paymentservice.payment.dto.payment;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
/**
 * 라이더 정보 전달
 */
public class RiderPaymentInfoResDto {
    private Long riderId;
    private String riderName;
    private String riderPhone;
    private Long shopId;
    private LocalDateTime completeTime;
}
