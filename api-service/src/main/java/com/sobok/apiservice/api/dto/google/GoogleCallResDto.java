package com.sobok.apiservice.api.dto.google;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sobok.apiservice.api.dto.social.SocialCallbackDto;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GoogleCallResDto implements SocialCallbackDto {
    private Long oauthId;
    private Long authId;
    @JsonProperty("isNew")
    private boolean isNew;
    private String email;
    private String name;
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
