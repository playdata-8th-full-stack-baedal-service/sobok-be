package com.sobok.postservice.post.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    }
}
