package com.sobok.deliveryservice.delivery.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "라이더 회원가입 요청 DTO")
public class RiderReqDto {

    @NotNull(message = "authId는 필수입니다.")
    @Schema(description = "인증 사용자 ID", example = "1234", required = true)
    private Long authId;

    @NotBlank(message = "이름은 필수입니다.")
    @Schema(description = "라이더 이름", example = "홍길동", required = true)
    private String name;

    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^01[016789]\\d{8}$", message = "전화번호는 - 없이 숫자만 11자리여야 합니다.")
    @Schema(description = "전화번호 (하이픈 없이 숫자 11자리)", example = "01012345678", required = true)
    private String phone;

    @NotBlank(message = "면허번호는 필수입니다.")
    @Pattern(regexp = "^\\d{12}$", message = "면허번호는 숫자 12자리여야 합니다.")
    @Schema(description = "면허번호 (숫자 12자리)", example = "123456789012", required = true)
    private String permissionNumber;
}
