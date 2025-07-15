package com.sobok.apiservice.api.dto.google;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GoogleCallResDto {
    private Long oauthId;
    private Long authId;
    @JsonProperty("isNew")
    private boolean isNew;
    private String email;
    private String name;
    private String picture;
}
