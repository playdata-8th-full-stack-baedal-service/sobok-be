package com.sobok.apiservice.api.dto.toss;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TossCancelReqDto {

    @Schema(description = "결제 취소 사유", example = "사용자 요청으로 인한 환불")
    private String cancelReason;
}
