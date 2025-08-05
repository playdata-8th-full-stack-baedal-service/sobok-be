package com.sobok.paymentservice.payment.dto.payment;

import com.sobok.paymentservice.common.enums.OrderState;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "관리자 주문 상세 응답 DTO")
/**
 * 주문 조회 응답용(필요한것만 AdminPaymentResDto 에서 필요한 부분만  걸름)
 */
public class AdminPaymentResponseDto {

    @Schema(description = "주문 번호", example = "ORDER_20250805_0001")
    private String orderId;

    @Schema(description = "총 결제 금액", example = "39000")
    private Long totalPrice;

    @Schema(description = "결제 방법", example = "카드결제")
    private String payMethod;

    @Schema(description = "주문 상태", example = "DELIVERED")
    private OrderState orderState;

    @Schema(description = "주문 생성 일시", example = "2025-08-01T15:23:01")
    private LocalDateTime createdAt;

    @Schema(description = "배달 완료 일시", example = "2025-08-01T16:15:00")
    private LocalDateTime completeTime;

    // 유저 정보
    @Schema(description = "유저 로그인 ID", example = "user123")
    private String loginId;

    @Schema(description = "유저 닉네임", example = "홍길동")
    private String nickname;

    @Schema(description = "주소(도로명)", example = "서울시 강남구 테헤란로 123")
    private String roadFull;

    @Schema(description = "주소 상세", example = "101동 202호")
    private String address;

    @Schema(description = "유저 전화번호", example = "010-1234-5678")
    private String phone;

    // 라이더 정보
    @Schema(description = "라이더 이름", example = "김배달")
    private String riderName;

    @Schema(description = "라이더 전화번호", example = "010-9876-5432")
    private String riderPhone;

    // 가게 정보
    @Schema(description = "가게 이름", example = "맛집 A")
    private String shopName;

    @Schema(description = "가게 주소", example = "서울시 강남구 역삼동 456-7")
    private String shopAddress;

    @Schema(description = "가게 사장 이름", example = "사장님")
    private String ownerName;

    @Schema(description = "가게 전화번호", example = "02-123-4567")
    private String shopPhone;

    // 요리 정보 리스트
    @Schema(description = "주문한 요리 리스트")
    private List<CookDetailWithIngredientsResDto> cooks;

}
