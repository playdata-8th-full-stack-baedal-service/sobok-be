package com.sobok.authservice.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Schema(description = "OAuth 인증 관련 응답 DTO")
public class OauthResDto {

    @Schema(description = "OAuth 식별자", example = "1001")
    private Long oauthId;

    @Schema(description = "인증 테이블 ID", example = "2002")
    private Long authId;

    @Schema(description = "신규 사용자 여부", example = "true")
    private boolean newUser;

    @Schema(description = "소셜 ID", example = "kakao_123456789")
    private String socialId;
}
