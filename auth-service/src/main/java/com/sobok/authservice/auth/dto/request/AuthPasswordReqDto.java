package com.sobok.authservice.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthPasswordReqDto {

    @Schema(description = "검증할 현재 비밀번호", example = "Test@1234", required = true)
    private String password;
}
