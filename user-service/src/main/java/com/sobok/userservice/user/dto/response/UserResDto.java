package com.sobok.userservice.user.dto.response;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResDto {
    private Long id;
    private Long authId;
    private String nickname;
    private String photo;
    private String email;
    private String phone;
}
