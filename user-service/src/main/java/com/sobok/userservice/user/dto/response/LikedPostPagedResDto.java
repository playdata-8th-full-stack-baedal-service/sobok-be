package com.sobok.userservice.user.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * 사용자가 좋아요를 누른 게시글 Id 목록을 페이징 형태로 응답
 */
public class LikedPostPagedResDto {
    private List<Long> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;
}