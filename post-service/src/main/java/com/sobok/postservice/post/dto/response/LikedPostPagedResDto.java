package com.sobok.postservice.post.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * 사용자가 좋아요한 게시글 목록
 */
public class LikedPostPagedResDto {
    private List<Long> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;
}