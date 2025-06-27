package com.sobok.authservice.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResDto {

    private Long id;

    private String authId;

    private String nickname;

}
