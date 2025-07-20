package com.sobok.apiservice.api.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sobok.apiservice.api.dto.social.SocialCallbackDto;
import lombok.*;

@Builder
@Data
public class KakaoCallResDto implements SocialCallbackDto {
    private Long oauthId;
    private Long authId;
    private boolean newUser;
    private String nickname;
    private String profileImage;
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
