package com.sobok.paymentservice.payment.dto.delivery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AcceptOrderReqDto {
    private Long paymentId;
    private Long riderId;
}
