package com.sobok.authservice.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "비밀번호 복구 요청 DTO")
public class RecoverReqDto {

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Schema(description = "새로 설정할 비밀번호", example = "newPassword123!", required = true)
    private String password;
}
