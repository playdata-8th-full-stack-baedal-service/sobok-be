package com.sobok.postservice.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "게시물 리스트 응답 DTO")
/**
 * 게시글 조회
 */
public class PostListResDto {

    @Schema(description = "게시물 ID", example = "1")
    private Long postId;

    @Schema(description = "게시물 제목", example = "맛있는 김치찌개 후기")
    private String title;

    @Schema(description = "요리 이름", example = "김치찌개")
    private String cookName;

    @Schema(description = "작성자 닉네임", example = "요리왕")
    private String nickName;

    @Schema(description = "작성자 사용자 ID", example = "101")
    private Long userId;

    @Schema(description = "게시물 좋아요 수", example = "25")
    private Long likeCount;

    @Schema(description = "썸네일 이미지 URL", example = "https://d3c5012dwkvoyc.cloudfront.net/food/bb1687e0-ac92-4db3-bb7f-df837d34851bpexels-robinstickel-70497.jpg")
    private String thumbnail;

    @Schema(description = "마지막 수정 시간", example = "2025-08-05T15:30:00")
    private LocalDateTime updatedAt;
}