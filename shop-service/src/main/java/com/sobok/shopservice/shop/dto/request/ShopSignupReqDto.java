package com.sobok.shopservice.shop.dto.request;


import lombok.*;

@Getter
@Setter
@ToString
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
