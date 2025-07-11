package com.sobok.shopservice.shop.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * 요리별로 좋아요 순으로 조회
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
        private int likeCount;
    }
}
