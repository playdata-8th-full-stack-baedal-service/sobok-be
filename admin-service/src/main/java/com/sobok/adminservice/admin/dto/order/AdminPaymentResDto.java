package com.sobok.adminservice.admin.dto.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sobok.adminservice.common.enums.OrderState;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
/**
 * rider 이름 조회, 유저 정보 조회, 가게 정보 조회 용
 */
public class AdminPaymentResDto {
    // 결제 정보
    private String orderId;
    private Long totalPrice;
    private String payMethod;
    private OrderState orderState;
    private Long createdAt;
    private Long userAddressId;

    // 유저 정보
    private String loginId;
    private String nickname;
    private String roadFull;
    private String address;
    private String phone;

    // 라이더 정보
    private String riderName;
    private Long riderId;
    private Long id;
    private String riderPhone;

    // 가게 정보
    private Long shopId;
    private String shopName;
    private String shopAddress;
    private String ownerName;
    private String shopPhone;

    // 요리 정보
    private List<CookDetailWithIngredientsResDto> cooks;

}