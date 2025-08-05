package com.sobok.apiservice.api.dto.google;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sobok.apiservice.api.dto.social.SocialCallbackDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@Schema(description = "Google 로그인 결과 응답 DTO")
public class GoogleCallResDto implements SocialCallbackDto {

    @Schema(description = "OAuth 고유 ID", example = "1234567890")
    private Long oauthId;

    @Schema(description = "인증 시스템 내 유저 ID", example = "1001")
    private Long authId;

    @JsonProperty("isNew")
    @Schema(description = "신규 회원 여부", example = "true")
    private boolean isNew;

    @Schema(description = "사용자 이메일", example = "user@example.com")
    private String email;

    @Schema(description = "사용자 이름(닉네임)", example = "홍길동")
    private String name;

    @Schema(description = "사용자 프로필 사진 URL", example = "https://example.com/image.jpg")
    private String picture;

    // 인터페이스 구현
    @Override
    public boolean isNew() {
        return this.isNew;
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
        return this.name; // name = nickname
    }

    @Override
    public String getEmail() {
        return this.email;
    }
}