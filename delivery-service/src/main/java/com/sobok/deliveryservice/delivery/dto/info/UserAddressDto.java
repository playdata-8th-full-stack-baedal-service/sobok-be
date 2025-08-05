package com.sobok.deliveryservice.delivery.dto.info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "사용자 주소 정보 DTO")
public class UserAddressDto {

    @Schema(description = "주소 ID", example = "456")
    private Long id;

    @Schema(description = "도로명 주소 전체", example = "서울특별시 강남구 테헤란로 123")
    private String roadFull;

    @Schema(description = "상세 주소", example = "101동 202호")
    private String addrDetail;
}
