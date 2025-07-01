package com.sobok.authservice.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopSignupReqDto {
    private Long authId;
    private String shopName;
    private String ownerName;
    private String phone;
    private String roadFull;
}
