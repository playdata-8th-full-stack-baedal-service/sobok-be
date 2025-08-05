package com.sobok.apiservice.api.dto.kakao;

import com.sobok.apiservice.common.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthSignupReqDto {

    @Schema(description = "소셜에서 제공된 사용자 ID", example = "123456789")
    private Long id;

    @Schema(description = "로그인 ID", example = "sobok_user")
    private String loginId;

    @Schema(description = "비밀번호", example = "password123!")
    private String password;

    @Schema(description = "사용자 역할", example = "ROLE_USER")
    private Role role;

    @Schema(description = "계정 활성 상태", example = "Y")
    private String active;
}