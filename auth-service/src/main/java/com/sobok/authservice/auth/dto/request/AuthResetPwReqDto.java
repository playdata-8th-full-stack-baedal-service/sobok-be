package com.sobok.authservice.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResetPwReqDto {

    @Schema(description = "비밀번호를 재설정할 사용자 ID", example = "42")
    private Long authId;

    @Schema(description = "사용자가 입력한 인증번호", example = "123456")
    private String inputCode;

    @NotBlank(message = "비밀번호는 필수 입니다.")
    @Pattern(
            regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}",
            message = "비밀번호는 영문 대,소문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 8자 ~ 16자의 비밀번호여야 합니다."
    )
    @Schema(
            description = "새로 설정할 비밀번호",
            example = "NewPass@123",
            minLength = 8,
            maxLength = 16,
            required = true
    )
    private String newPassword;
}
