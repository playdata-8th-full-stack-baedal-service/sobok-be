package com.sobok.postservice.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "특정 요리 게시글 목록 응답 DTO")
/**
 * 요리에 대한 게시글 정보
 */
public class CookPostGroupResDto {

    @Schema(description = "요리 ID", example = "1")
    private Long cookId;

    @Schema(description = "게시물 요약 정보 리스트")
    private List<PostSummaryDto> posts;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "게시물 요약 DTO")
    public static class PostSummaryDto {

        @Schema(description = "게시물 ID", example = "10")
        private Long postId;

        @Schema(description = "게시물 제목", example = "김치찌개는 역시 돼지고기")
        private String title;

        @Schema(description = "썸네일 이미지 URL", example = "https://image-url.com/thumb1.jpg")
        private String thumbnail;

        @Schema(description = "게시물 좋아요 수", example = "12")
        private Long likeCount;

        @Schema(description = "게시물 수정일", example = "2025-08-05T13:45:00")
        private LocalDateTime updatedAt;
    }
}
