package com.sobok.authservice.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthFindIdResDto {
    @Schema(description = "찾은 로그인 아이디", example = "user123")
    private String loginId;
}
