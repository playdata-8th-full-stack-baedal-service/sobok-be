package com.sobok.authservice.auth.dto.info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthUserAddressDto {

    @Schema(description = "주소 ID", example = "10")
    String id;

    @Schema(description = "도로명 주소", example = "서울특별시 강남구 테헤란로 123")
    String roadFull;

    @Schema(description = "상세 주소", example = "3층 301호")
    String addrDetail;
}
