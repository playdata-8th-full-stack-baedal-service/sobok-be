package com.sobok.authservice.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthFindIdReqDto {
//    String username;
    String userPhoneNumber;
    String userInputCode;
}
