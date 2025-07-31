package com.sobok.deliveryservice.delivery.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import jakarta.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiderReqDto {

    @NotNull(message = "authId는 필수입니다.")
    private Long authId;

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^01[016789]\\d{8}$", message = "전화번호는 - 없이 숫자만 11자리여야 합니다.")
    private String phone;

    @NotBlank(message = "면허번호는 필수입니다.")
    @Pattern(regexp = "^\\d{12}$", message = "면허번호는 숫자 12자리여야 합니다.")
    private String permissionNumber;

}
