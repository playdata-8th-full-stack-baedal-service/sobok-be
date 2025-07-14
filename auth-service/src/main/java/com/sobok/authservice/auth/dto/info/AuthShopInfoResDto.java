package com.sobok.authservice.auth.dto.info;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthShopInfoResDto extends AuthBaseInfoResDto {
    String shopName;
    String phone;
    String roadFull;
    String ownerName;

    public AuthShopInfoResDto(Long authId, String loginId, String shopName, String phone, String roadFull, String ownerName) {
        super(authId, loginId);
        this.shopName = shopName;
        this.phone = phone;
        this.roadFull = roadFull;
        this.ownerName = ownerName;
    }
}
