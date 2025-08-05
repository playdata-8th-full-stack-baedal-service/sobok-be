package com.sobok.deliveryservice.delivery.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
/**
 * 라이더 이름 조회용 Dto
 */
@Schema(description = "라이더 주문 완료 정보 조회 DTO")
public class RiderPaymentInfoResDto {

    @Schema(description = "라이더 ID", example = "2001")
    private Long riderId;

    @Schema(description = "라이더 이름", example = "홍길동")
    private String riderName;

    @Schema(description = "라이더 전화번호", example = "010-1234-5678")
    private String riderPhone;

    @Schema(description = "가게 ID", example = "101")
    private Long shopId;

    @Schema(description = "주문 완료 시간", example = "2025-08-04T14:30:00")
    private LocalDateTime completeTime;
}