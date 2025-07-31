package com.sobok.userservice.user.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserSignupReqDto {
    private Long authId;
    private String nickname;
    private String phone;
    private String email;
    private String photo;
    private String roadFull;
    private String addrDetail;
}
