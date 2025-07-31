package com.sobok.paymentservice.payment.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 요리 정보
public class CookInfoResDto {
    private Long cookId;
    private String name;
    private String thumbnail;
    private String active;
}
