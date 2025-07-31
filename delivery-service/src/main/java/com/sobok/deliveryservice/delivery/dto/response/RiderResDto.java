package com.sobok.deliveryservice.delivery.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RiderResDto {

    private Long id;
    private Long authId;
    private String name;
    private String phone;
    private String permissionNumber;
    private String active;
}
