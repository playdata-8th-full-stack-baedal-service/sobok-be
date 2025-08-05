package com.sobok.apiservice.api.dto.google;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GoogleResDto {

    @Schema(description = "애플리케이션이 Google API 요청을 승인하기 위해 보내는 토큰", example = "ya29.a0AfH6S...")
    private String access_token; // 애플리케이션이 Google API 요청을 승인하기 위해 보내는 토큰

    @Schema(description = "Access Token의 남은 수명", example = "3599")
    private String expires_in;   // Access Token의 남은 수명

    @Schema(description = "새 액세스 토큰을 얻는 데 사용할 수 있는 토큰", example = "1//0gY0J...")
    private String refresh_token;    // 새 액세스 토큰을 얻는 데 사용할 수 있는 토큰

    @Schema(description = "OAuth 범위", example = "openid email profile")
    private String scope;

    @Schema(description = "반환된 토큰 유형", example = "Bearer")
    private String token_type;   // 반환된 토큰 유형(Bearer 고정)

    @Schema(description = "ID 토큰", example = "eyJhbGciOiJSUzI1NiIsImtpZCI6...")
    private String id_token;
}