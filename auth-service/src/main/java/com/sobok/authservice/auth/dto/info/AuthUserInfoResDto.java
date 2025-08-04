package com.sobok.authservice.auth.dto.info;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class AuthUserInfoResDto extends AuthBaseInfoResDto {
    String nickname;
    String email;
    String phone;
    String photo;
    List<AuthUserAddressDto> addresses;
    boolean socialUser;

    public AuthUserInfoResDto(Long authId, String loginId, String nickname, String email, String phone, String photo, List<AuthUserAddressDto> addresses) {
        super(authId, loginId);
        this.nickname = nickname;
        this.email = email;
        this.phone = phone;
        this.photo = photo;
        this.addresses = addresses;
    }
}
