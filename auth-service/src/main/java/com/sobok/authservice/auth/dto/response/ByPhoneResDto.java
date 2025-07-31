package com.sobok.authservice.auth.dto.response;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ByPhoneResDto {
    private Long id;
    private Long authId;
    private String phone;
}
