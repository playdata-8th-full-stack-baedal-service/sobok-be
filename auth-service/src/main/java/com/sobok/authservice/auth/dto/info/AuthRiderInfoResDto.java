package com.sobok.authservice.auth.dto.info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRiderInfoResDto extends AuthBaseInfoResDto {

    @Schema(description = "라이더 이름", example = "홍길동")
    String name;

    @Schema(description = "라이더 전화번호", example = "01012345678")
    String phone;

    @Schema(description = "라이더 면허 번호", example = "123456789012")
    String permissionNumber;

    public AuthRiderInfoResDto(Long authId, String loginId, String name, String phone, String permissionNumber) {
        super(authId, loginId);
        this.name = name;
        this.phone = phone;
        this.permissionNumber = permissionNumber;
    }
}
