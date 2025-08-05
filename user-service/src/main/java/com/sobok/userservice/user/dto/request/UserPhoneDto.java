package com.sobok.userservice.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPhoneDto {
    @NotBlank(message = "전화번호는 필수 입니다.")
    @Pattern(regexp = "^01[016789]\\d{8}$", message = "전화번호는 - 없이 숫자만 11자리여야 합니다.")
    @Schema(
            description = "사용자 전화번호 (숫자만, 하이픈(-) 없이 11자리)",
            example = "01012345678"
    )
    String phone;

    @Pattern(regexp = "^\\d{6}$", message = "6자리 숫자를 입력해주세요.")
    @Schema(
            description = "사용자가 입력한 인증 코드 (6자리 숫자)",
            example = "123456"
    )
    String userInputCode;
}
