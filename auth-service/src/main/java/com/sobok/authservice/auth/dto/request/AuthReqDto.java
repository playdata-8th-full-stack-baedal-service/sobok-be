package com.sobok.authservice.auth.dto.request;


import com.sobok.authservice.common.enums.Role;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthReqDto {

    private String loginId;
    private String password;
    private Role role;

    private String nickname;
    private String phone;
    private String email;
    private String photo;

    private String roadFull;
    private String addrDetail;

}
