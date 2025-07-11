package com.sobok.deliveryservice.delivery.dto.info;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAddressDto {
    private Long id;
    private String roadFull;
    private String addrDetail;
}