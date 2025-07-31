package com.sobok.authservice.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor

/*
 라이더 정보 전달 dto
 */
public class AuthRiderInfoResDto {
    private Long authId;
    private String loginId;
    private String active;
}

