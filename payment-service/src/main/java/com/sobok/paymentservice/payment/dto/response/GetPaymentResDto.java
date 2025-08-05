package com.sobok.paymentservice.payment.dto.response;

import com.sobok.paymentservice.common.enums.OrderState;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "주문 내역 응답 DTO")
public class GetPaymentResDto {
    @Schema(description = "결제 ID", example = "101")
    private Long paymentId;

    @Schema(description = "주문 번호", example = "ORDER_20250805_0001")
    private String orderId;

    @Schema(description = "총 결제 금액", example = "39000")
    private Long totalPrice;

    @Schema(description = "주문 상태", example = "COMPLETED")
    private OrderState orderState;

    @Schema(description = "주문 생성 일시", example = "2025-08-01T15:23:01")
    private LocalDateTime createdAt;

    @Schema(description = "주문한 요리 목록")
    private List<Cook> cook;

    @Data
    @Builder
    @Schema(description = "요리 DTO")
    public static class Cook {
        @Schema(description = "요리 ID", example = "1")
        private Long cookId;

        @Schema(description = "요리 이름", example = "김치찌개")
        private String cookName;

        @Schema(description = "요리 썸네일 이미지 파일명", example = "kimchistew001.png")
        private String thumbnail;
    }
}

