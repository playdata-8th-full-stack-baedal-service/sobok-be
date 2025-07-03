package com.sobok.cookservice.cook.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngreReqDto {

    @NotBlank(message = "식재료 이름은 필수입니다.")
    private String ingreName;

    @NotBlank(message = "식재료 가격은 필수입니다.")
    private String price;

    @NotBlank(message = "식재료 원산지는 필수입니다.")
    private String origin;

    @NotBlank(message = "식재료 단위는 필수입니다.")
    private String unit;

}
