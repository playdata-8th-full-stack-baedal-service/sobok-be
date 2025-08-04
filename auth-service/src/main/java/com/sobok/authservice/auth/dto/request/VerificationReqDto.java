package com.sobok.authservice.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "전화번호 인증 요청 DTO")
public class VerificationReqDto {

    @Schema(description = "전화번호 (- 없이 11자리)", example = "01012345678", required = true)
    @NotBlank(message = "전화번호는 필수 입니다.")
    @Pattern(regexp = "^01[016789]\\d{8}$", message = "전화번호는 - 없이 숫자만 11자리여야 합니다.")
    private String phoneNumber;

    @Schema(description = "입력한 인증 코드", example = "482910", required = true)
    private String inputCode;
}
