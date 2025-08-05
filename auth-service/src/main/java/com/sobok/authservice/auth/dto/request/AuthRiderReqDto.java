package com.sobok.authservice.auth.dto.request;

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
@Schema(description = "라이더 회원가입 요청 DTO")
public class AuthRiderReqDto {

    @NotBlank(message = "아이디는 필수 입니다.")
    @Schema(description = "로그인 아이디", example = "rider_001", required = true)
    private String loginId;

    @NotBlank(message = "비밀번호는 필수 입니다.")
    @Pattern(regexp="(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}",
            message = "비밀번호는 조건을 만족해야 합니다.")
    @Schema(description = "비밀번호 (영문 대소문자+숫자+특수문자 포함, 8~16자)", example = "Test@1234", required = true)
    private String password;

    @NotBlank(message = "이름은 필수 입니다.")
    @Schema(description = "라이더 이름", example = "김라이더", required = true)
    private String name;

    @NotBlank(message = "전화번호는 필수 입니다.")
    @Pattern(regexp = "^01[016789]\\d{8}$", message = "전화번호는 - 없이 숫자만 11자리여야 합니다.")
    @Schema(description = "전화번호 (숫자만)", example = "01012345678", required = true)
    private String phone;

    @NotBlank(message = "면허 번호는 필수 입니다.")
    @Pattern(regexp = "^\\d{12}$", message = "면허 번호는 숫자만 12자리여야 합니다.")
    @Schema(description = "면허 번호 (숫자 12자리)", example = "123456789012", required = true)
    private String permissionNumber;
}
