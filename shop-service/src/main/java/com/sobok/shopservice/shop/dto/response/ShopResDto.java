package com.sobok.shopservice.shop.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "관리자 전용 가게 응답 DTO")
// 관리자 전용 가게 전제 조회
public class ShopResDto {
    @Schema(description = "가게 ID", example = "5")
    private Long id;  // 목록 번호

    @Schema(description = "지점 이름", example = "소복 서초점")
    private String shopName;  // 지점 이름

    @Schema(description = "도로명 주소", example = "서초구 효령로 133")
    private String roadFull;  // 주소

    @Schema(description = "대표자 이름", example = "홍길동")
    private String ownerName;  // 대표자 이름

    @Schema(description = "전화번호", example = "0511234567")
    private String phone;  // 전화번호
}