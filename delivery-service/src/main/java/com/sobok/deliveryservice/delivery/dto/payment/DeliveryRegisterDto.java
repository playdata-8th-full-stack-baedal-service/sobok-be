package com.sobok.deliveryservice.delivery.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@Schema(description = "배달 등록 요청 DTO")
public class DeliveryRegisterDto {

    @Schema(description = "가게 ID", example = "101")
    private Long shopId;

    @Schema(description = "결제 ID", example = "1001")
    private Long paymentId;
}
