package com.sobok.authservice.auth.dto.info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "인증 기본 정보 응답 DTO")
public abstract class AuthBaseInfoResDto {

    @Schema(description = "인증 정보 ID", example = "1")
    Long authId;

    @Schema(description = "로그인 아이디", example = "user123")
    String loginId;
}
