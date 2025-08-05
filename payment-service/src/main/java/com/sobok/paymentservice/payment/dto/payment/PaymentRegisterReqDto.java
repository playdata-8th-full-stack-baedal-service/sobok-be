package com.sobok.paymentservice.payment.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@Schema(description = "주문 등록 요청 DTO")
public class PaymentRegisterReqDto {
    @Schema(description = "주문 고유 ID", example = "20250801x6ATP328")
    private String orderId;

    @Schema(description = "총 결제 금액", example = "19000")
    private Long totalPrice;

    @Schema(description = "라이더 요청사항", example = "문 앞에 놓아주세요.")
    private String riderRequest;

    @Schema(description = "사용자 주소 ID", example = "7")
    private Long userAddressId;

    @Schema(description = "장바구니 요리 ID 리스트", example = "[11, 12, 13]")
    private List<Long> cartCookIdList;
}
