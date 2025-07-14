package com.sobok.postservice.post.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
/**
 * 게시글 좋아요 등록, 해제
 */
public class PostIdRequestDto {
    private Long postId;
}
