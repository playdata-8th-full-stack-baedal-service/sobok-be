package com.sobok.apiservice.api.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sobok.apiservice.api.dto.social.SocialCallbackDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Data
public class KakaoCallResDto implements SocialCallbackDto {
    @Schema(description = "OAuth 식별자", example = "1234567890")
    private Long oauthId;

    @Schema(description = "내부 인증 시스템 ID", example = "1")
    private Long authId;

    @Schema(description = "신규 가입자 여부", example = "true")
    private boolean newUser;

    @Schema(description = "사용자 닉네임", example = "sobok_user")
    private String nickname;

    @Schema(description = "프로필 이미지 URL", example = "https://k.kakaocdn.net/dn/...")
    private String profileImage;

    @Schema(description = "이메일 주소", example = "user@example.com")
    private String email;

    // 인터페이스 메서드 구현
    @Override
    public boolean isNew() {
        return this.newUser;
    }

    @Override
    public Long getOauthId() {
        return this.oauthId;
    }

    @Override
    public Long getAuthId() {
        return this.authId;
    }

    @Override
    public String getNickname() {
        return this.nickname;
    }

    @Override
    public String getEmail() {
        return this.email;
    }
}
