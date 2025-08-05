package com.sobok.authservice.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Schema(description = "가게 등록 요청 DTO")
public class AuthShopReqDto {

    @NotBlank(message = "아이디는 필수 입니다.")
    @Schema(description = "로그인 아이디", example = "shop_001", required = true)
    private String loginId;

    @NotBlank(message = "비밀번호는 필수 입니다.")
    @Pattern(regexp="(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}",
            message = "비밀번호는 조건을 만족해야 합니다.")
    @Schema(description = "비밀번호 (영문 대소문자+숫자+특수문자 포함, 8~16자)", example = "Shop@1234", required = true)
    private String password;

    @NotBlank(message = "가게 이름은 필수 입니다.")
    @Schema(description = "가게 이름", example = "맛있는김밥", required = true)
    private String shopName;

    @NotBlank(message = "가게 주인 이름 작성은 필수 입니다.")
    @Schema(description = "가게 대표자 이름", example = "이사장", required = true)
    private String ownerName;

    @NotBlank(message = "전화번호는 필수 입니다.")
    @Pattern(regexp = "^01[016789]\\d{8}$", message = "전화번호는 - 없이 숫자만 11자리여야 합니다.")
    @Schema(description = "전화번호 (숫자만)", example = "01098765432", required = true)
    private String phone;

    @NotBlank(message = "도로명 주소는 필수 입니다.")
    @Schema(description = "도로명 주소", example = "서울특별시 강남구 테헤란로 123", required = true)
    private String roadFull;

    @DecimalMin(value = "-90.0") @DecimalMax(value = "90.0")
    @Schema(description = "가게 위치 위도", example = "37.5665")
    private Double latitude;

    @DecimalMin(value = "-180.0") @DecimalMax(value = "180.0")
    @Schema(description = "가게 위치 경도", example = "126.9780")
    private Double longitude;
}
