package com.sobok.cookservice.cook.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "식재료 수정 요청 DTO")
public class IngreEditReqDto {

    @Schema(description = "식재료 ID", example = "1", required = true)
    @NotNull(message = "식재료 Id는 필수입니다.")
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
