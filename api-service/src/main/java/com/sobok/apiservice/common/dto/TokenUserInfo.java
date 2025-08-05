package com.sobok.apiservice.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "토큰에서 추출한 사용자 정보 DTO")
public class TokenUserInfo {

    @Schema(description = "인증 ID", example = "1")
    private Long id;

    @Schema(description = "사용자 역할", example = "USER")
    private String role;

    @Schema(description = "일반 사용자 ID", example = "101")
    private Long userId;

    @Schema(description = "배달원 ID", example = "202")
    private Long riderId;

    @Schema(description = "가게 ID", example = "303")
    private Long shopId;
}