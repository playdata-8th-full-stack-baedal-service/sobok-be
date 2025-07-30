package com.sobok.postservice.post.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * 요리에 대한 게시글 정보
 */
public class CookPostGroupResDto {
    private Long cookId;
    private List<PostSummaryDto> posts;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PostSummaryDto {
        private Long postId;
        private String title;
        private String thumbnail;
        private Long likeCount;
        private LocalDateTime updatedAt;
    }
}
