package com.sobok.authservice.auth.dto.request;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthRiderReqDto {

    private String loginId;
    private String password;
    private String name;
    private String phone;
    private String permissionNumber;
}
