package com.sobok.deliveryservice.delivery.dto.response;

import com.sobok.deliveryservice.common.enums.OrderState;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "라이더 배달 주문 응답 DTO")
public class DeliveryOrderResDto {
    @Schema(description = "주문 번호", example = "ORD20250001")
    private String orderId;

    @Schema(description = "결제 ID", example = "20001")
    private Long paymentId;

    @Schema(description = "가게 이름", example = "맛집가게")
    private String shopName;

    @Schema(description = "가게 도로명 주소", example = "서울시 강남구 테헤란로 123")
    private String shopRoadFull;

    @Schema(description = "도로명 주소", example = "서울시 강남구 테헤란로 123")
    private String roadFull;

    @Schema(description = "상세 주소", example = "3층 301호")
    private String addrDetail;

    @Schema(description = "주문 상태")
    private OrderState orderState;

    @Schema(description = "배달 완료 시간", example = "2025-08-04T12:00:00")
    private LocalDateTime completeTime;
}
