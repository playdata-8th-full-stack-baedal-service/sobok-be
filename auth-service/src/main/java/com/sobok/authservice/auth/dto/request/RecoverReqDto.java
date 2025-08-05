package com.sobok.authservice.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecoverReqDto {

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
}
