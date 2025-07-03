package com.sobok.cookservice.cook.dto.response;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngreResDto {
//    private Long id;
    private String ingreName;
    private String price;
    private String origin;
    private String unit;

}
