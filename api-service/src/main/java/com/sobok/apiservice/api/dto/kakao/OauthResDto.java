package com.sobok.apiservice.api.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OauthResDto {

    @Schema(description = "OAuth 식별자", example = "123456789")
    private Long oauthId;

    @Schema(description = "내부 인증 시스템 ID", example = "1")
    private Long authId;

    @Schema(description = "신규 가입자 여부", example = "true")
    private boolean newUser;

    @Schema(description = "소셜 고유 ID", example = "kakao_112233")
    private String socialId;
}