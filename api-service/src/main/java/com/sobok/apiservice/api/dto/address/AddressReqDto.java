package com.sobok.apiservice.api.dto.address;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressReqDto {
    private String roadFull;
    private String addrDetail;
}
