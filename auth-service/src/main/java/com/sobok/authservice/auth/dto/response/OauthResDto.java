package com.sobok.authservice.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OauthResDto {
    private Long id;
    private Long authId;
    @JsonProperty("isNew")
    private boolean isNew;
//    private String nickname;
}
