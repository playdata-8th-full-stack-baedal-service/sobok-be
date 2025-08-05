package com.sobok.apiservice.api.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class KakaoUserResDto {
    @Schema(description = "카카오 사용자 고유 ID", example = "123456789")
    private Long id;

    @Schema(description = "카카오 연결 시간", example = "2025-08-01T14:23:00")
    @JsonProperty("connected_at")
    private LocalDateTime connectedAt;

    @Schema(description = "카카오 계정 정보")
    @JsonProperty("kakao_account")
    private KakaoAccount account;

    @Schema(description = "카카오 프로필 정보")
    private Properties properties;

    @Setter
    @Getter
    @ToString
    public static class Properties {

        @Schema(description = "사용자 닉네임", example = "sobok_user")
        private String nickname;

        @Schema(description = "프로필 이미지 URL", example = "https://...")
        @JsonProperty("profile_image")
        private String profileImage;

        @Schema(description = "썸네일 이미지 URL", example = "https://...")
        @JsonProperty("thumbnail_image")
        private String thumbnailImage;
    }

    @Getter
    @Setter
    @ToString
    public static class KakaoAccount {

        @Schema(description = "사용자 이메일", example = "user@example.com")
        private String email;
    }
}