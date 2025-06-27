package com.sobok.authservice.auth.dto;


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
    private String role;

    private String nickname;
    private String phone;
    private String email;
    private String photo;
    private String roadFull;
    private String addrDetail;
    private Double latitude;
    private Double longitude;
}
