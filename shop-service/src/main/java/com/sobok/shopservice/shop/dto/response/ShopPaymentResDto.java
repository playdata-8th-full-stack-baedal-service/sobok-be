package com.sobok.shopservice.shop.dto.response;

import com.sobok.shopservice.common.enums.OrderState;
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
@Schema(description = "주문 결제 정보 DTO")
public class ShopPaymentResDto {
    @Schema(description = "결제 ID", example = "1001")
    private Long paymentId;
    @Schema(description = "주문 ID", example = "20250801x1vZ2A4p")
    private String orderId;
    @Schema(description = "주문 상태", example = "DELIVERY_COMPLETE", allowableValues = {"ORDER_PENDING", "ORDER_COMPLETE", "PREPARING_INGREDIENTS",
            "READY_FOR_DELIVERY", "DELIVERY_ASSIGNED", "DELIVERING", "DELIVERY_COMPLETE"})
    private OrderState orderState;
    @Schema(description = "최근 업데이트 시간", example = "2025-08-04T15:30:00")
    private LocalDateTime updatedAt;
}
