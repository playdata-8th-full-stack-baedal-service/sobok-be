package com.sobok.userservice.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAddressReqDto {
    @Schema(description = "도로명 주소", example = "서울특별시 강남구 테헤란로 123")
    private String roadFull;
    @Schema(description = "상세 주소", example = "101동 202호")
    private String addrDetail;
}
