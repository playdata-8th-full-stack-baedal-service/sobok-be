package com.sobok.apiservice.api.dto.kakao;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AuthLoginResDto {

    @Schema(description = "사용자 ID", example = "1")
    Long id;

    @Schema(description = "사용자 권한", example = "ROLE_USER")
    String role;

    @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    String accessToken;

    @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    String refreshToken;

    @Schema(description = "복구 대상 여부", example = "false")
    boolean recoveryTarget;
}