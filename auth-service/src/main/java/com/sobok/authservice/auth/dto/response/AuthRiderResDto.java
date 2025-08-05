package com.sobok.authservice.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Schema(description = "라이더 기본 정보 DTO")
public class AuthRiderResDto {

    @Schema(description = "라이더 ID", example = "1001")
    private Long id;

    @Schema(description = "라이더 이름", example = "홍길동")
    private String name;
}
