package com.sobok.authservice.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthLoginResDto {
    @Schema(description = "사용자 ID", example = "1")
    Long id;

    @Schema(description = "사용자 역할", example = "USER")
    String role;

    @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    String accessToken;

    @Schema(description = "리프레시 토큰", example = "dGhpc2lzYXJlZnJlc2h0b2tlbg==")
    String refreshToken;

    @Schema(description = "계정 복구 대상 여부", example = "false")
    boolean recoveryTarget;
}
