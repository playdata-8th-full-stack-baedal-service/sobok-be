package com.sobok.authservice.auth.dto.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class AuthBaseInfoResDto {
    String loginId;
}
