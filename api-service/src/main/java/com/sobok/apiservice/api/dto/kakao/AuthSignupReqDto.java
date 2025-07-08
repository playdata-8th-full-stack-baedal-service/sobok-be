package com.sobok.apiservice.api.dto.kakao;

import com.sobok.apiservice.common.enums.Role;
import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthSignupReqDto {
    private Long id;
    private String loginId;
    private String password;
    private Role role;
    private String active;
}
