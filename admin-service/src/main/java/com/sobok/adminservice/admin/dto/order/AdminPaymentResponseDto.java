package com.sobok.adminservice.admin.dto.order;

import com.sobok.adminservice.common.enums.OrderState;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
/**
 * 주문 조회 응답용(필요한것만 AdminPaymentResDto 에서 필요한 부분만  걸름)
 */
public class AdminPaymentResponseDto {
    private String orderId;
    private Long totalPrice;
    private String payMethod;
    private OrderState orderState;
    private Long createdAt;

    // 유저 정보
    private String nickname;
    private String roadFull;
    private String address;

    // 라이더 정보
    private String riderName;

    // 가게 정보
    private String shopName;
    private String shopAddress;
}
