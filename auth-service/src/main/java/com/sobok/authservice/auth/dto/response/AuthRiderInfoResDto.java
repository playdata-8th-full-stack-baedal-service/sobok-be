package com.sobok.authservice.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/*
 라이더 정보 전달 dto
*/
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class AuthRiderInfoResDto {
    @Schema(description = "인증 아이디", example = "101")
    private Long authId;

    @Schema(description = "로그인 아이디", example = "rider01")
    private String loginId;

    @Schema(description = "활성화 상태 (Y/N)", example = "Y")
    private String active;
}
