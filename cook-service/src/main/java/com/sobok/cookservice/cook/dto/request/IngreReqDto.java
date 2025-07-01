package com.sobok.cookservice.cook.dto.request;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngreReqDto {

    private String ingreName;
    private String price;
    private String origin;
    private String unit;

}
