package com.sobok.authservice.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthUserResDto {

    @Schema(description = "회원 아이디", example = "user123")
    private Long id;

    @Schema(description = "닉네임", example = "유저1")
    private String nickname;

}
