package com.sobok.authservice.auth.dto.info;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AuthRiderInfoResDto extends AuthBaseInfoResDto {
    String name;
    String phone;
    String permissionNumber;

    public AuthRiderInfoResDto(String loginId, String name, String phone,  String permissionNumber) {
        super(loginId);
        this.name = name;
        this.phone = phone;
        this.permissionNumber = permissionNumber;
    }
}
