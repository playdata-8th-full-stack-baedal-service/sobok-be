package com.sobok.apiservice.api.dto.toss;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TossPayReqDto {

    @Schema(description = "토스 결제 키", example = "tk_abcdefgh123456789")
    private String paymentKey;

    @Schema(description = "주문 ID", example = "ORDER_12345")
    private String orderId;

    @Schema(description = "결제 금액", example = "12000")
    private String amount;

    @Override
    public String toString() {
        return "TossPayReqDto : " +
                "orderId = " + orderId +
                ", amount = " + amount +
                ", paymentKey = " + paymentKey;
    }
}

