package com.sobok.deliveryservice.delivery.dto.payment;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
/**
 * 라이더 이름 조회용 Dto
 */
public class RiderPaymentInfoResDto {
    private String riderName;
    private Long riderId;
    private String riderPhone;
}