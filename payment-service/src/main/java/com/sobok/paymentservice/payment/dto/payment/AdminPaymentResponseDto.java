package com.sobok.paymentservice.payment.dto.payment;

import com.sobok.paymentservice.common.enums.OrderState;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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
    private LocalDateTime createdAt;
    private LocalDateTime completeTime;

    // 유저 정보
    private String loginId;
    private String nickname;
    private String roadFull;
    private String address;
    private String phone;

    // 라이더 정보
    private String riderName;
    private String riderPhone;

    // 가게 정보
    private String shopName;
    private String shopAddress;
    private String ownerName;
    private String shopPhone;

    // 요리 정보
    private List<CookDetailWithIngredientsResDto> cooks;

}
