package com.sobok.authservice.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OauthResDto {
    private Long oauthId;
    private Long authId;
    private boolean newUser;
    private String socialId;
}
