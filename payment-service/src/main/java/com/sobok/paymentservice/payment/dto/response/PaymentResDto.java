package com.sobok.paymentservice.payment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "장바구니 조회 요청 DTO")
// 장바구니 조회 응답
public class PaymentResDto {
    @Schema(description = "주문한 사용자 ID", example = "1")
    private Long userId;
    @Schema(description = "장바구니 내 요리 리스트")
    private List<PaymentItemResDto> items;
}
