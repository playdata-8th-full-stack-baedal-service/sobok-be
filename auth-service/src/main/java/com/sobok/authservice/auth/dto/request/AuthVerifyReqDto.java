package com.sobok.authservice.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthVerifyReqDto {
    @Pattern(regexp="^[a-zA-Z0-9_]{4,20}$", message = "아이디 형식이 유효하지 않습니다.")
    private String loginId;
    private String userPhoneNumber;
}
