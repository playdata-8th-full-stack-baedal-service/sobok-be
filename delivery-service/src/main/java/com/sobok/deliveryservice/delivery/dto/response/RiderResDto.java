package com.sobok.deliveryservice.delivery.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "라이더 간략 응답 DTO")
public class RiderResDto {

    @Schema(description = "라이더 ID", example = "1001")
    private Long id;

    @Schema(description = "라이더 이름", example = "홍길동")
    private String name;

    @Schema(description = "전화번호", example = "01012345678")
    private String phone;

    @Schema(description = "면허 번호", example = "123456789012")
    private String permissionNumber;

    @Schema(description = "활성화 상태 (Y/N)", example = "N")
    private String active;
}
