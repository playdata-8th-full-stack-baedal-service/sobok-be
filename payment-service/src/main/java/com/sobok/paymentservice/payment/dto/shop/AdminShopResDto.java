package com.sobok.paymentservice.payment.dto.shop;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * 주문 전체 조회용 가게 정보
 */
public class AdminShopResDto {
    private Long shopId;
    private String shopName;
    private String shopAddress;
    private String shopPhone;
    private String ownerName;
}
