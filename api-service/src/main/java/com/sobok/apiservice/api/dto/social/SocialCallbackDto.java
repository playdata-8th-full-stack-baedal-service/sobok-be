package com.sobok.apiservice.api.dto.social;

import io.swagger.v3.oas.annotations.media.Schema;

public interface SocialCallbackDto {
    @Schema(description = "신규 가입자 여부")
    boolean isNew();

    @Schema(description = "OAuth 식별자 (소셜에서 제공하는 고유 키)")
    Long getOauthId();

    @Schema(description = "사용자 닉네임")
    String getNickname();

    @Schema(description = "사용자 이메일")
    String getEmail();

    @Schema(description = "내부 인증 시스템의 Auth ID")
    Long getAuthId();
}