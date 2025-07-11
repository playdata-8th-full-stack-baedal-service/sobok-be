package com.sobok.postservice.post.dto.request;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * 게시글 수정
 */
public class PostUpdateReqDto {
    private Long postId;
    private String title;
    private String content;
    private List<PostImageDto> images;
}
