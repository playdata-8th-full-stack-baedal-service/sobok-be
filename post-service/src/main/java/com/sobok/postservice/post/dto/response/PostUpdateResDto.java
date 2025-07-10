package com.sobok.postservice.post.dto.response;

import com.sobok.postservice.post.dto.request.PostImageDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * 게시글 수정
 */
public class PostUpdateResDto {
    private Long postId;
    private String title;
    private String content;
    private List<PostImageDto> images;
}
