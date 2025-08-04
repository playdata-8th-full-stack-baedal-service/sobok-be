package com.sobok.deliveryservice.delivery.dto.info;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthRiderInfoResDto {
    String loginId;
    String name;
    String phone;
    String permissionNumber;
    Long authId;
}
