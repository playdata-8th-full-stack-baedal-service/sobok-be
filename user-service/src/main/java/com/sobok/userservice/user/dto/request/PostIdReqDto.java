package com.sobok.userservice.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * 게시글 좋아요 등록, 해제
 */
public class PostIdReqDto {
    private Long postId;
}
