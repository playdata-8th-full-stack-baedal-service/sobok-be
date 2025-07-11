package com.sobok.postservice.post.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * 게시글 조회
 */
public class PostListResDto {
    private Long postId;
    private String title;
    private String cookName;
    private String userNickname;
    private Long userId;
    private int likeCount;
    private String thumbnail;
    private Long updatedAt;
}