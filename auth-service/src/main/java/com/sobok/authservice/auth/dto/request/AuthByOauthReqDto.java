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
public class AuthByOauthReqDto {
    private Long oauthId;

    @NotBlank(message = "별명은 필수 입니다.")
    private String nickname;

    @NotBlank(message = "전화번호는 필수 입니다.")
    @Pattern(regexp = "^01[016789]\\d{8}$", message = "전화번호는 - 없이 숫자만 11자리여야 합니다.")
    private String phone;

    private String photo;

    private String roadFull;
    private String addrDetail;

    private String loginId;
    private String password;
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "유효한 이메일 형식이어야 합니다."
    )
    private String email;
    private String inputCode;
}
