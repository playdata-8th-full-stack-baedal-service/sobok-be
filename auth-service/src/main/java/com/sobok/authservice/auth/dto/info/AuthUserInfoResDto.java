package com.sobok.authservice.auth.dto.info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class AuthUserInfoResDto extends AuthBaseInfoResDto {

    @Schema(description = "유저 별명 (닉네임)", example = "배달왕자")
    String nickname;

    @Schema(description = "이메일 주소", example = "user@example.com")
    String email;

    @Schema(description = "전화번호", example = "01012345678")
    String phone;

    @Schema(description = "프로필 사진 URL", example = "https://example.com/photo.jpg")
    String photo;

    @Schema(description = "주소 목록")
    List<AuthUserAddressDto> addresses;

    @Schema(description = "소셜 로그인 사용자 여부", example = "false")
    boolean socialUser;

    public AuthUserInfoResDto(Long authId, String loginId, String nickname, String email, String phone, String photo, List<AuthUserAddressDto> addresses) {
        super(authId, loginId);
        this.nickname = nickname;
        this.email = email;
        this.phone = phone;
        this.photo = photo;
        this.addresses = addresses;
    }
}
