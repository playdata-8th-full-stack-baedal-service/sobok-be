package com.sobok.deliveryservice.delivery.dto.payment;

import com.sobok.deliveryservice.common.enums.OrderState;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "가게별 주문 조회 응답 DTO")
public class ShopPaymentResDto {

    @Schema(description = "결제 ID", example = "1001")
    private Long paymentId;

    @Schema(description = "주문 ID", example = "ORD20250804")
    private String orderId;

    @Schema(description = "주문 상태")
    private OrderState orderState;

    @Schema(description = "주문 생성 일시", example = "2025-08-01T12:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "주문 수정 일시", example = "2025-08-02T15:00:00")
    private LocalDateTime updatedAt;

    @Schema(description = "사용자 주소 ID", example = "3001")
    private Long userAddressId;
}
