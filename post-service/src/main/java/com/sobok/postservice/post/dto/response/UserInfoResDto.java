package com.sobok.postservice.post.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * 게시글 조회 용
 */
public class UserInfoResDto {
    private Long userId;
    private String nickname;
}
