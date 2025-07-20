package com.sobok.userservice.user.dto.response;

import lombok.*;

@Getter
@AllArgsConstructor
@Setter
@NoArgsConstructor
@Builder
/**
 * 게시글 종아요 응답
 */
public class UserLikeResDto {
    private Long postId;
}