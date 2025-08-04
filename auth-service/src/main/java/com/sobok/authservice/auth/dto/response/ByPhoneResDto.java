package com.sobok.authservice.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ByPhoneResDto {

    @Schema(description = "내부 식별자 ID", example = "123")
    private Long id;

    @Schema(description = "인증 테이블 ID", example = "456")
    private Long authId;

    @Schema(description = "전화번호", example = "01012345678")
    private String phone;
}
