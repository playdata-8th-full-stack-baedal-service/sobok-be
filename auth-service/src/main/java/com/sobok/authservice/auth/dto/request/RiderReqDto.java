package com.sobok.authservice.auth.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiderReqDto {
    private Long authId;
    private String name;
    private String phone;
    private String permissionNumber;
}