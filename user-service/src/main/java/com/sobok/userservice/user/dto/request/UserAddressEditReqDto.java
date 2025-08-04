package com.sobok.userservice.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "사용자가 수정할 주소 정보 DTO")
public class UserAddressEditReqDto {
    @Schema(description = "수정할 주소의 ID", example = "5", required = true)
    private Long addressId;
    @NotBlank(message = "주소는 필수 입니다.")
    @Schema(description = "도로명 전체 주소", example = "서울특별시 강남구 테헤란로 123", required = true)
    private String roadFull;
    @Schema(description = "상세 주소", example = "삼성빌딩 2층")
    private String addrDetail;
}
