package com.sobok.apiservice.api.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Data
public class KakaoCallResDto {
    private Long oauthId;
    private Long authId;
    @JsonProperty("isNew")
    private boolean isNew;
    private KakaoCallResDto.Properties properties;

    @JsonProperty("kakao_account")
    private KakaoCallResDto.KakaoAccount account;

    @Data
    @Builder
    public static class Properties {
        private String nickname;
        @JsonProperty("profile_image")
        private String profileImage;
        @JsonProperty("thumbnail_image")
        private String thumbnailImage;
    }

    @Data
    @Builder
    public static class KakaoAccount {
        private String email;
    }
}
