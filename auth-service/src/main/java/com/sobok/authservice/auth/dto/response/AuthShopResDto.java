package com.sobok.authservice.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthShopResDto {

    private Long id;
    private String shopName;
    private String ownerName;
}
