package com.sobok.authservice.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "통합 회원가입 요청 DTO (소셜 & 일반)")
public class UnifiedSignupReqDto {

    @Schema(description = "로그인 ID", example = "user123", required = false)
    private String loginId;

    @Schema(description = "비밀번호", example = "Password!123", required = false)
    private String password;

    @Schema(description = "소셜 OAuth ID", example = "1029348", required = false)
    private Long oauthId;

    @Schema(description = "닉네임", example = "길동이", required = true)
    private String nickname;

    @Schema(description = "이메일", example = "user@example.com", required = false)
    private String email;

    @Schema(description = "전화번호", example = "01012345678", required = true)
    private String phone;

    @Schema(description = "프로필 사진 URL", example = "https://example.com/image.jpg", required = false)
    private String photo;

    @Schema(description = "도로명 주소", example = "서울특별시 강남구 테헤란로 123", required = false)
    private String roadFull;

    @Schema(description = "상세 주소", example = "301호", required = false)
    private String addrDetail;

    @Schema(description = "입력한 인증 코드", example = "482910", required = false)
    private String inputCode;
}
