package com.sobok.cookservice.cook.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "식재료 생성 요청 DTO")
public class IngreReqDto {

    @Schema(description = "식재료 ID", example = "1")
    private Long id;

    @Schema(description = "식재료 이름", example = "감자", required = true)
    @NotBlank(message = "식재료 이름은 필수입니다.")
    private String ingreName;

    @Schema(description = "가격", example = "1000", required = true)
    @NotNull
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private Integer price;

    @Schema(description = "원산지", example = "국내산", required = true)
    @NotBlank(message = "식재료 원산지는 필수입니다.")
    private String origin;

    @Schema(description = "단위", example = "1", required = true)
    @NotNull
    @Min(value = 1, message = "단위는 1 이상이어야 합니다.")
    private Integer unit;
}
