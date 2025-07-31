package com.sobok.authservice.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthLoginResDto {
    Long id;
    String role;
    String accessToken;
    String refreshToken;
    boolean recoveryTarget;
}
