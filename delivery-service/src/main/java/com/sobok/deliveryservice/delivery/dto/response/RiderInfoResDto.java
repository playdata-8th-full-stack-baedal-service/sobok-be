package com.sobok.deliveryservice.delivery.dto.response;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class RiderInfoResDto {

    private Long id;
    private Long authId;
    private String name;
    private String phone;
    private String permissionNumber;
    private String active;
    private String loginId;

}
