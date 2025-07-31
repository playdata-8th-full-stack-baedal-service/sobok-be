package com.sobok.authservice.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class AuthReissueReqDto {
    Long id;
    String refreshToken;
}
