package com.sobok.authservice.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "로그인 요청 DTO")
public class AuthLoginReqDto {
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "아이디 형식이 유효하지 않습니다.")
    @Schema(description = "로그인 아이디", example = "user123", required = true)
    String loginId;

    @Schema(description = "비밀번호", example = "Password12345!", required = true)
    String password;
}
