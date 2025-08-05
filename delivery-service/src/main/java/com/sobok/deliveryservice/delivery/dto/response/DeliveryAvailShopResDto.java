package com.sobok.deliveryservice.delivery.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "배달 가능 가게 정보 응답 DTO")
public class DeliveryAvailShopResDto {
    @Schema(description = "가게 ID", example = "501")
    private Long shopId;

    @Schema(description = "가게 이름", example = "맛집가게")
    private String shopName;

    @Schema(description = "가게 도로명 주소", example = "서울시 강남구 테헤란로 123")
    private String roadFull;
}
