package com.sobok.deliveryservice.delivery.dto.info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "라이더 인증 정보 응답 DTO")
public class AuthRiderInfoResDto {

    @Schema(description = "로그인 아이디", example = "rider123")
    private String loginId;

    @Schema(description = "라이더 이름", example = "홍길동")
    private String name;

    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phone;

    @Schema(description = "면허 번호", example = "12-345678-901234")
    private String permissionNumber;

    @Schema(description = "인증 정보 ID", example = "123")
    private Long authId;
}
