package com.sobok.apiservice.api.dto.google;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Google 사용자 정보 응답 DTO")
public class GoogleDetailResDto {

    @JsonProperty("id") // 구글 응답의 "id" 필드를 sub 필드에 매핑
    @Schema(description = "Google 사용자 고유 ID (sub)", example = "1234567890")
    private String sub;

    @Schema(description = "이메일", example = "user@example.com")
    private String email;

    @JsonProperty("verified_email")
    @Schema(description = "이메일 인증 여부", example = "true")
    private Boolean emailVerified;

    @Schema(description = "사용자 이름", example = "홍길동")
    private String name;

    @JsonProperty("given_name")
    @Schema(description = "사용자 이름(이름 부분)", example = "길동")
    private String givenName;

    @JsonProperty("family_name")
    @Schema(description = "사용자 이름(성 부분)", example = "홍")
    private String familyName;

    @Schema(description = "프로필 사진 URL", example = "https://example.com/image.jpg")
    private String picture;

    @Schema(description = "로케일", example = "ko")
    private String locale;
}
