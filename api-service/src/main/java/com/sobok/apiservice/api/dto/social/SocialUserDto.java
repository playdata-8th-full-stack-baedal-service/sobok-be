package com.sobok.apiservice.api.dto.social;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SocialUserDto {

    @Schema(description = "소셜 고유 ID (예: 카카오/구글 사용자 고유 ID)", example = "2313241234")
    private String socialId;      // 소셜의 고유 ID

    @Schema(description = "소셜 플랫폼 이름 (예: KAKAO, GOOGLE)", example = "KAKAO")
    private String provider;      // "KAKAO", "GOOGLE" 등
}
