package com.sobok.apiservice.api.dto.social;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SocialUserDto {
    private String socialId;      // 소셜의 고유 ID
    private String provider;      // "KAKAO", "GOOGLE" 등
}
