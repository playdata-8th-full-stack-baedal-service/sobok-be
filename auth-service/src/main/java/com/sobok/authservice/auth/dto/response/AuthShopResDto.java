package com.sobok.authservice.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthShopResDto {

    @Schema(description = "가게 ID", example = "2001")
    private Long id;

    @Schema(description = "가게 이름", example = "소복마트")
    private String shopName;

    @Schema(description = "가게 주인 이름", example = "김사장")
    private String ownerName;
}
