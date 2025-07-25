package com.sobok.cookservice.cook.dto.request;

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
    private Integer price;

    @NotBlank(message = "식재료 원산지는 필수입니다.")
    private String origin;

    @NotNull
    private Integer unit;

}
