package com.sobok.cookservice.cook.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngreReqDto {

    private Long id;

    @NotBlank(message = "식재료 이름은 필수입니다.")
    private String ingreName;

    @NotNull
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private Integer price;

    @NotBlank(message = "식재료 원산지는 필수입니다.")
    private String origin;

    @NotNull
    @Min(value = 1, message = "단위는 1 이상이어야 합니다.")
    private Integer unit;

}
