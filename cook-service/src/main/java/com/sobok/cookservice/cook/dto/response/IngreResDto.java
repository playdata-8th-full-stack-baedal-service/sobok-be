package com.sobok.cookservice.cook.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 식재료 상세 정보 응답 DTO
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "식재료 상세 정보 DTO")
public class IngreResDto {

    @Schema(description = "식재료 ID", example = "1")
    private Long id;

    @Schema(description = "식재료 이름", example = "감자")
    private String ingreName;

    @Schema(description = "가격", example = "1000")
    private Integer price;

    @Schema(description = "원산지", example = "국내산")
    private String origin;

    @Schema(description = "단위", example = "1")
    private Integer unit;
}
