package com.sobok.authservice.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthByOauthReqDto {

    @Schema(description = "소셜 OAuth 식별자", example = "1001")
    private Long oauthId;

    @NotBlank(message = "별명은 필수 입니다.")
    @Schema(description = "사용자 별명", example = "소복이", required = true)
    private String nickname;

    @NotBlank(message = "전화번호는 필수 입니다.")
    @Pattern(regexp = "^01[016789]\\d{8}$", message = "전화번호는 - 없이 숫자만 11자리여야 합니다.")
    @Schema(description = "전화번호 (- 없이 입력)", example = "01012345678", required = true)
    private String phone;

    @Schema(description = "사용자 프로필 사진 URL", example = "https://example.com/image.jpg")
    private String photo;

    @Schema(description = "도로명 주소", example = "서울특별시 강남구 테헤란로 123")
    private String roadFull;

    @Schema(description = "상세 주소", example = "101동 202호")
    private String addrDetail;

    @Schema(description = "로그인 아이디", example = "social_user01")
    private String loginId;

    @Schema(description = "비밀번호", example = "password123!")
    private String password;

    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "유효한 이메일 형식이어야 합니다."
    )
    @Schema(description = "이메일 주소", example = "user@example.com")
    private String email;

    @Schema(description = "입력된 인증번호", example = "482913")
    private String inputCode;
}

