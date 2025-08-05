package com.sobok.authservice.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "아이디 찾기 요청 DTO")
public class AuthFindIdReqDto {

    @Pattern(
            regexp = "^01[016789]\\d{8}$",
            message = "전화번호 형식이 유효하지 않습니다."
    )
    @Schema(
            description = "사용자의 휴대전화 번호 (- 없이 입력)",
            example = "01012345678"
    )
    private String userPhoneNumber;

    @Schema(
            description = "입력한 인증번호",
            example = "928371"
    )
    private String userInputCode;
}
