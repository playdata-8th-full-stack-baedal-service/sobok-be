package com.sobok.deliveryservice.delivery.dto.payment;

import com.sobok.deliveryservice.common.dto.TokenUserInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "라이더 주문 상태 변경 요청 DTO")
public class RiderChangeOrderStateReqDto {

    @Schema(description = "사용자 정보 (인증 토큰 정보)")
    private TokenUserInfo userInfo;

    @Schema(description = "결제 ID", example = "1001")
    private Long paymentId;
}
