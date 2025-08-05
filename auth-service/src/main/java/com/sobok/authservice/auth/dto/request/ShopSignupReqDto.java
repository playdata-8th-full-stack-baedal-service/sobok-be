package com.sobok.authservice.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "가게 회원가입 요청 DTO")
public class ShopSignupReqDto {

    @Schema(description = "인증 고유 ID", example = "1", required = true)
    private Long authId;

    @Schema(description = "가게 이름", example = "김밥천국", required = true)
    private String shopName;

    @Schema(description = "사장 이름", example = "홍길동", required = true)
    private String ownerName;

    @Schema(description = "전화번호", example = "01012345678", required = true)
    private String phone;

    @Schema(description = "도로명 주소", example = "서울특별시 강남구 테헤란로 123", required = true)
    private String roadFull;
}
