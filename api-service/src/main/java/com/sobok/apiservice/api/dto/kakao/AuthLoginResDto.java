package com.sobok.apiservice.api.dto.kakao;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AuthLoginResDto {
    Long id;
    String role;
    String accessToken;
    String refreshToken;
    boolean recoveryTarget;
}
