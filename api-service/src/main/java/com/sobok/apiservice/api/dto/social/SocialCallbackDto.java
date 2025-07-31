package com.sobok.apiservice.api.dto.social;

public interface SocialCallbackDto {
    boolean isNew();
    Long getOauthId();
    String getNickname();
    String getEmail();
    Long getAuthId();
}
