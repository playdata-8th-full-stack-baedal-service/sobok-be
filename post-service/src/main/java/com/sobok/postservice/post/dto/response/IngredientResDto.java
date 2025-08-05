package com.sobok.postservice.post.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "재료 상세 정보 DTO")
public class IngredientResDto {

    @Schema(description = "재료 ID", example = "1")
    private Long ingredientId;

    @Schema(description = "재료 이름", example = "양파")
    private String ingredientName;

    @Schema(description = "단위", example = "10")
    private Integer unit;

    @Schema(description = "수량", example = "4")
    private int quantity;

    @Schema(description = "가격 (원 단위)", example = "2")
    private int price;

    @Schema(description = "원산지", example = "국내산")
    private String origin;
    @JsonIgnore
    private boolean defaultFlag;
}