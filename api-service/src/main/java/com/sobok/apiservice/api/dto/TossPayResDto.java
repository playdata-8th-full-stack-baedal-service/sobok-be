package com.sobok.apiservice.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TossPayResDto {
    private String mid;
    private String orderId;
    private String paymentKey;
}
