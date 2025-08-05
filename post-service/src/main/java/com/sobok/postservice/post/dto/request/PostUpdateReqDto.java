package com.sobok.postservice.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "게시글 수정 요청 DTO")
/**
 * 게시글 수정
 */
public class PostUpdateReqDto {

    @Schema(description = "게시글 ID", example = "123")
    private Long postId;

    @Schema(description = "게시글 제목", example = "김치찌개 맛있게 먹는 법")
    private String title;

    @Schema(description = "게시글 내용", example = "<p>삼겹살 식당 묵은지 김치찌개 스타일</p>")
    private String content;

    @Schema(description = "게시글 이미지 리스트")
    private List<PostImageDto> images;
}
