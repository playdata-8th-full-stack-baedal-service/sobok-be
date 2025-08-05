package com.sobok.authservice.auth.dto.info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "가게 인증 정보 응답 DTO")
public class AuthShopInfoResDto extends AuthBaseInfoResDto {

    @Schema(description = "가게 이름", example = "소복마트")
    String shopName;

    @Schema(description = "가게 전화번호", example = "01098765432")
    String phone;

    @Schema(description = "가게 도로명 주소", example = "서울특별시 강남구 테헤란로 456")
    String roadFull;

    @Schema(description = "가게 주인 이름", example = "김사장")
    String ownerName;

    public AuthShopInfoResDto(Long authId, String loginId, String shopName, String phone, String roadFull, String ownerName) {
        super(authId, loginId);
        this.shopName = shopName;
        this.phone = phone;
        this.roadFull = roadFull;
        this.ownerName = ownerName;
    }
}
