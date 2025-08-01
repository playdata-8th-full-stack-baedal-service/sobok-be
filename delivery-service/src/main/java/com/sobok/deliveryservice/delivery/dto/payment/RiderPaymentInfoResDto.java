package com.sobok.deliveryservice.delivery.dto.payment;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
/**
 * 라이더 이름 조회용 Dto
 */
public class RiderPaymentInfoResDto {
    private Long riderId;
    private String riderName;
    private String riderPhone;
    private Long shopId;
    private LocalDateTime completeTime;
}