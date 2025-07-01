package com.sobok.shopservice.shop.dto.response;


import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthShopResDto {

    private Long id;
    private String shopName;
    private String ownerName;

}
