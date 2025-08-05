package com.sobok.deliveryservice.delivery.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "주문 수락 요청 DTO")
public class AcceptOrderReqDto {

    @Schema(description = "결제 ID", example = "1001", required = true)
    private Long paymentId;

    @Schema(description = "라이더 ID", example = "2001", required = true)
    private Long riderId;
}
