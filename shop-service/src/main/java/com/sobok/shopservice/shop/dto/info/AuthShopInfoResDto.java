package com.sobok.shopservice.shop.dto.info;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthShopInfoResDto {
    String loginId;
    String shopName;
    String phone;
    String roadFull;
    String ownerName;
    Long authId;
}
