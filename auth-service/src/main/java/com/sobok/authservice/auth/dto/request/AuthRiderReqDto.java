package com.sobok.authservice.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthRiderReqDto {

    @NotBlank(message = "아이디는 필수 입니다.")
    private String loginId;

    @NotBlank(message = "비밀번호는 필수 입니다.")
    @Pattern(regexp="(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}",
            message = "비밀번호는 영문 대,소문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 8자 ~ 16자의 비밀번호여야 합니다.")
    private String password;

    @NotBlank(message = "이름은 필수 입니다.")
    private String name;

    @NotBlank(message = "전화번호는 필수 입니다.")
    @Pattern(regexp = "^01[016789]\\d{8}$", message = "전화번호는 - 없이 숫자만 11자리여야 합니다.")
    private String phone;

    @NotBlank(message = "면허 번호는 필수 입니다.")
    @Pattern(regexp = "^\\d{12}$", message = "면허 번호는 숫자만 12자리여야 합니다.")
    private String permissionNumber;
}
