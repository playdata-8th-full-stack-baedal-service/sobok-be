package com.sobok.deliveryservice.delivery.dto.response;

import com.sobok.deliveryservice.common.enums.OrderState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryOrderResDto {
    private String orderId;
    private Long paymentId;
    private String shopName;
    private String shopRoadFull;
    private String roadFull;
    private String addrDetail;
    private OrderState orderState;
    private LocalDateTime completeTime;
}
