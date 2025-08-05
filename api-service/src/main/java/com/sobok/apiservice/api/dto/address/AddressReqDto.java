package com.sobok.apiservice.api.dto.address;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "주소 요청 DTO")
public class AddressReqDto {

    @Schema(description = "도로명 주소 전체", example = "서울특별시 강남구 테헤란로 123")
    private String roadFull;

    @Schema(description = "상세 주소", example = "아파트 101동 1001호")
    private String addrDetail;
}