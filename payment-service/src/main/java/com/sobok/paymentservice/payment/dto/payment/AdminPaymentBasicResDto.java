package com.sobok.paymentservice.payment.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "관리자용 주문 기본 정보 DTO")
public class AdminPaymentBasicResDto {

    @Schema(description = "결제 ID", example = "101")
    private Long paymentId;

    @Schema(description = "주문 번호", example = "20250801x6ATP328")
    private String orderId;

    @Schema(description = "주문 생성 일시", example = "2025-08-01T15:23:01")
    private LocalDateTime createdAt;
}
