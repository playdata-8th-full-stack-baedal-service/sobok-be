package com.sobok.deliveryservice.delivery.dto.response;

import com.sobok.deliveryservice.common.enums.OrderState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryAvailOrderResDto {
    //가게 정보
    private Long shopId;
    private String shopName;
    private String shopRoadFull;
    //주문 정보
    private Long paymentId;
    private String orderId;
    private OrderState orderState;
    private Long createdAt;
    private String roadFull;
    private String addrDetail;
}
