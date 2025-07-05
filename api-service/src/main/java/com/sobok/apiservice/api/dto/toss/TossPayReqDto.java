package com.sobok.apiservice.api.dto.toss;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TossPayReqDto {
    private String orderId;
    private Long amount;
    private String paymentKey;

    @Override
    public String toString() {
        return "TossPayReqDto : " +
                "orderId = " + orderId +
                ", amount = " + amount +
                ", paymentKey = " + paymentKey;
    }
}

