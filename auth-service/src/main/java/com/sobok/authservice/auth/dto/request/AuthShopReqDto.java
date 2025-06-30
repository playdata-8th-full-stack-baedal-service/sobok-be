package com.sobok.authservice.auth.dto.request;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthShopReqDto {

    private String loginId;
    private String password;

    private String shopName; // 가게 이름
    private String ownerName; // 가게 주인 이름
    private String phone;
    private String roadFull; // 도로명 주소
    private Double latitude; // 위도
    private Double longitude; // 경도
}
