package com.sobok.adminservice.admin.dto.shop;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 관리자 전용 가게 전제 조회
public class ShopResDto {
    private Long id;          // 목록 번호
    private String shopName;  // 지점 이름
    private String roadFull;  // 주소
    private String ownerName; // 대표자 이름
    private String phone;     // 전화번호
}
