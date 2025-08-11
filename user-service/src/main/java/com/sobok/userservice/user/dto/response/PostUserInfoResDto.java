package com.sobok.userservice.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostUserInfoResDto {
    private Long userId;
    private String nickname;
    private String photo;
}
