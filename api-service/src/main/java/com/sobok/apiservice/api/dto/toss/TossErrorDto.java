package com.sobok.apiservice.api.dto.toss;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TossErrorDto {

    @Schema(description = "에러 코드", example = "INVALID_CARD_COMPANY")
    private String code;

    @Schema(description = "에러 메시지", example = "유효하지 않은 카드사입니다.")
    private String message;
}
