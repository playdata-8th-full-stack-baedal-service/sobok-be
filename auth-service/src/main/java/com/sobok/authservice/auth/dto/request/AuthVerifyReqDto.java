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
@Schema(description = "비밀번호 찾기 인증 요청 DTO")
public class AuthVerifyReqDto {

    @Schema(description = "아이디", example = "user123", required = true)
    @Pattern(regexp="^[a-zA-Z0-9_]{4,20}$", message = "아이디 형식이 유효하지 않습니다.")
    private String loginId;

    @Schema(description = "전화번호", example = "01012345678", required = true)
    private String userPhoneNumber;
}
