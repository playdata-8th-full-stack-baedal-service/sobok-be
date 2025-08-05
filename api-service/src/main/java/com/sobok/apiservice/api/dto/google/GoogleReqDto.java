package com.sobok.apiservice.api.dto.google;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Google OAuth 요청 DTO")
public class GoogleReqDto {

    @Schema(description = "애플리케이션 클라이언트 ID", example = "1234567890-abc.apps.googleusercontent.com")
    private String clientId;    // 애플리케이션의 클라이언트 ID

    @Schema(description = "OAuth 리다이렉트 URI", example = "https://yourapp.com/oauth/google/callback")
    private String redirectUri; // Google 로그인 후 redirect 위치

    @Schema(description = "클라이언트 보안 비밀", example = "abcdefg123456")
    private String clientSecret;    // 클라이언트 보안 비밀

    @Schema(description = "응답 타입", example = "code")
    private String responseType;    // Google OAuth 2.0 엔드포인트가 인증 코드를 반환하는지 여부

    @Schema(description = "OAuth 동의 범위", example = "profile email")
    private String scope;   // OAuth 동의범위

    @Schema(description = "OAuth 인증 코드", example = "4/0AX4XfWh...")
    private String code;

    @Schema(description = "액세스 유형", example = "offline")
    private String accessType;  // 사용자가 브라우저에 없을 때 애플리케이션이 액세스 토큰을 새로 고칠 수 있는지 여부

    @Schema(description = "인증 그랜트 타입", example = "authorization_code")
    private String grantType;

    @Schema(description = "상태 값", example = "xyz123")
    private String state;

    @Schema(description = "추가 권한 부여 포함 여부", example = "true")
    private String includeGrantedScopes;    // 애플리케이션이 컨텍스트에서 추가 범위에 대한 액세스를 요청하기 위해 추가 권한 부여를 사용

    @Schema(description = "로그인 힌트", example = "user@example.com")
    private String loginHint;   // 애플리케이션이 인증하려는 사용자를 알고 있는 경우 이 매개변수를 사용하여 Google 인증 서버에 힌트를 제공

    @Schema(description = "프롬프트 옵션", example = "consent")
    private String prompt;  // default: 처음으로 액세스를 요청할 때만 사용자에게 메시지가 표시
}

