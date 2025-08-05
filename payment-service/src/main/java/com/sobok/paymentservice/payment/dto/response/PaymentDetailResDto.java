package com.sobok.paymentservice.payment.dto.response;

import com.sobok.paymentservice.common.enums.OrderState;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "주문 상세 내역 응답 DTO")
public class PaymentDetailResDto {

    @Schema(description = "결제 ID", example = "101")
    private Long paymentId;

    @Schema(description = "주문 번호", example = "20250801x6ATP328")
    private String orderId;

    @Schema(description = "총 결제 금액", example = "19000")
    private Long totalPrice;

    @Schema(description = "주문 상태", example = "DELIVERY_COMPLETE")
    private OrderState orderState;

    @Schema(description = "주문 생성 일시", example = "2025-08-01T15:23:01")
    private LocalDateTime createdAt;

    @Schema(description = "결제 수단", example = "간편결제")
    private String payMethod;

    @Schema(description = "배달 요청 사항", example = "문 앞에 놓아주세요.")
    private String riderRequest;

    @Schema(description = "주문한 요리 목록")
    private List<PaymentItemResDto> items;

    @Schema(description = "도로명 주소", example = "서울시 강남구 테헤란로 123")
    private String roadFull;

    @Schema(description = "상세 주소", example = "101동 202호")
    private String addrDetail;

    @Schema(description = "가게 이름", example = "소복 강남점")
    private String shopName;

    @Schema(description = "가게 주소", example = "서울시 강남구 역삼동 456-7")
    private String shopAddress;

    // 배달 완료된 시간
    @Schema(description = "배달 완료 시간", example = "2025-08-01T16:15:00")
    private LocalDateTime completeTime;
}
