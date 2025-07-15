package com.sobok.authservice.auth.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UnifiedSignupReqDto {
    private String loginId;
    private String password;
    private Long oauthId; // 소셜이면 값이 존재
    private String nickname;
    private String email;
    private String phone;
    private String photo;
    private String roadFull;
    private String addrDetail;
}