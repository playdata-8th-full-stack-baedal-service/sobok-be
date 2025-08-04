package com.sobok.authservice.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "라이더 정보 등록 요청 DTO")
public class RiderReqDto {

    @Schema(description = "회원 고유 ID", example = "1", required = true)
    private Long authId;

    @Schema(description = "이름", example = "홍길동", required = true)
    private String name;

    @Schema(description = "전화번호", example = "01012345678", required = true)
    private String phone;

    @Schema(description = "면허 번호", example = "123456789012", required = true)
    private String permissionNumber;
}
