package com.sobok.paymentservice.payment.dto.payment;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
/**
 * 라이더 정보 전달
 */
public class RiderPaymentInfoResDto {
    private String riderName;
    private String riderPhone;
}
