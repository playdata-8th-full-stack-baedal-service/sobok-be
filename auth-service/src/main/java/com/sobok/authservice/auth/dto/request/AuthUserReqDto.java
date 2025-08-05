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
@Schema(description = "사용자 회원가입 요청 DTO")
public class AuthUserReqDto {

    @NotBlank(message = "아이디는 필수 입니다.")
    @Pattern(regexp="^[a-zA-Z0-9_]{4,20}$",
            message = "아이디는 4~20자의 영문, 숫자, 해당 특수문자(_)만 가능합니다.")
    @Schema(description = "회원 아이디", example = "user123", required = true)
    private String loginId;

    @NotBlank(message = "비밀번호는 필수 입니다.")
    @Pattern(regexp="(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}",
            message = "비밀번호는 영문 대,소문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 8자 ~ 16자의 비밀번호여야 합니다.")
    @Schema(description = "비밀번호", example = "password123!", required = true)
    private String password;

    @NotBlank(message = "별명은 필수 입니다.")
    @Schema(description = "유저 별명 (닉네임)", example = "배달왕자", required = true)
    private String nickname;

    @NotBlank(message = "전화번호는 필수 입니다.")
    @Pattern(regexp = "^01[016789]\\d{8}$", message = "전화번호는 - 없이 숫자만 11자리여야 합니다.")
    @Schema(description = "전화번호", example = "01012345678", required = true)
    private String phone;

    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "유효한 이메일 형식이어야 합니다."
    )
    @Schema(description = "이메일 주소", example = "user@example.com")
    private String email;

    @Schema(description = "프로필 사진 URL 또는 Base64", example = "https://example.com/photo.png")
    private String photo;

    @NotBlank(message = "주소는 필수 입니다.")
    @Schema(description = "도로명 주소", example = "서울특별시 강남구 테헤란로 123", required = true)
    private String roadFull;

    @Schema(description = "상세 주소", example = "3층 301호")
    private String addrDetail;

    @Schema(description = "인증번호 (전화번호 인증용)", example = "012345")
    private String inputCode;

}
