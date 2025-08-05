package com.sobok.userservice.user.dto.info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "사용자 주소 응답 DTO")
public class UserAddressDto {
    @Schema(description = "주소 ID", example = "1")
    private Long id;
    @Schema(description = "도로명 주소", example = "서울특별시 강남구 테헤란로 123")
    private String roadFull;
    @Schema(description = "상세 주소", example = "아파트 101동 202호")
    private String addrDetail;
}