package com.sobok.userservice.user.dto.request;

import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "주소는 필수 입니다.")
    private String roadFull;
    private String addrDetail;
}
