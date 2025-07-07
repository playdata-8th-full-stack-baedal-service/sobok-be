package com.sobok.shopservice.shop.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 관리자 전용 가게 전제 조회
public class ShopResDto {
    private Long id;          // 목록 번호
    private Long shopId;      //
    private String shopName;  // 지점 이름
    private String roadFull;  // 주소
    private String ownerName; // 대표자 이름
    private String phone;     // 전화번호
}