package com.sobok.authservice.auth.dto.info;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthUserAddressDto {
    String id;
    String roadFull;
    String addrDetail;
}
