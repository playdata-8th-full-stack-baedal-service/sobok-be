package com.sobok.authservice.auth.dto.request;

import com.sobok.authservice.common.enums.Role;
import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthSignupReqDto {
    private String loginId;
    private String password;
    private Role role;
    private String active;
    private Long id;
}
