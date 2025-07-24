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
    private String email;
    private String inputCode;
}
