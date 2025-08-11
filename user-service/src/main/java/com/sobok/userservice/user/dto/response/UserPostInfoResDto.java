package com.sobok.userservice.user.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * 게시글 조회용(유저 정보)
 */
public class UserPostInfoResDto {
    private Long userId;
    private String nickname;
    private Long authId;
    private String photo;
}
