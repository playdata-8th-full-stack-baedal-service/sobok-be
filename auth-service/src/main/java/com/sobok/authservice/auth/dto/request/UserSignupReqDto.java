package com.sobok.authservice.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "일반 사용자 회원가입 요청 DTO")
public class UserSignupReqDto {

    @Schema(description = "인증 ID", example = "1", required = true)
    private Long authId;

    @Schema(description = "닉네임", example = "길동이", required = true)
    private String nickname;

    @Schema(description = "전화번호", example = "01012345678", required = true)
    private String phone;

    @Schema(description = "이메일 주소", example = "user@example.com", required = false)
    private String email;

    @Schema(description = "프로필 사진 URL", example = "https://example.com/photo.jpg", required = false)
    private String photo;

    @Schema(description = "도로명 주소", example = "서울특별시 강남구 테헤란로 123", required = false)
    private String roadFull;

    @Schema(description = "상세 주소", example = "301호", required = false)
    private String addrDetail;
}
