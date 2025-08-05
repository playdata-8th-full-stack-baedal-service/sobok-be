package com.sobok.deliveryservice.delivery.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "배달 완료 응답 DTO")
public class DeliveryResDto {
    @Schema(description = "가게 ID", example = "501")
    private Long shopId;

    @Schema(description = "배달 완료 시간", example = "2025-08-04T12:00:00")
    private LocalDateTime completeTime;

    @Schema(description = "라이더 ID", example = "1001")
    private Long riderId;
}
