package com.sobok.userservice.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSignupReqDto {
    private Long authId;
    private String nickname;
    private String phone;
    private String email;
    private String photo;
    private String roadFull;
    private String addrDetail;
}
