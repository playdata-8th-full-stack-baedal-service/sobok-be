package com.sobok.deliveryservice.delivery.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryResDto {
    private Long shopId;
    private LocalDateTime completeTime;
    private Long riderId;
}
