package com.sobok.userservice.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAddressEditReqDto {
    private Long addressId;
    private String roadFull;
    private String addrDetail;
}
