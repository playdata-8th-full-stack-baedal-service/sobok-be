package com.sobok.shopservice.shop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryAvailShopResDto {
    private Long shopId;
    private String shopName;
    private String roadFull;
}
