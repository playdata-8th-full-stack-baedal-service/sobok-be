package com.sobok.authservice.auth.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class SmsReqDto {
    private String phone;
}
