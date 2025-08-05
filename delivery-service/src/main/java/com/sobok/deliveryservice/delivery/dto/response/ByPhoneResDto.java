package com.sobok.deliveryservice.delivery.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "전화번호 기준 라이더 응답 DTO")
public class ByPhoneResDto {
    @Schema(description = "라이더 ID", example = "101")
    private Long id;

    @Schema(description = "인증 사용자 ID", example = "1001")
    private Long authId;

    @Schema(description = "전화번호", example = "01012345678")
    private String phone;
}
