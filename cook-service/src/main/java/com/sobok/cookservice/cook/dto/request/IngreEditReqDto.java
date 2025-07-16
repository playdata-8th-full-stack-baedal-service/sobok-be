package com.sobok.cookservice.cook.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngreEditReqDto {
    @NotNull(message = "식재료 Id는 필수입니다.")
    private Long id;
    private String ingreName;
    private Integer price;
    private String origin;
    private String unit;
}
