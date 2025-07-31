package com.sobok.apiservice.api.dto.google;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GoogleDetailResDto {
    @JsonProperty("id") // 구글 응답의 "id" 필드를 sub 필드에 매핑
    private String sub;

    private String email;

    @JsonProperty("verified_email")
    private Boolean emailVerified;

    private String name;

    @JsonProperty("given_name")
    private String givenName;

    @JsonProperty("family_name")
    private String familyName;

    private String picture;

    private String locale;
}
