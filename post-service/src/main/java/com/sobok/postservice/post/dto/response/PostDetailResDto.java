package com.sobok.postservice.post.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "게시글 상세 응답 DTO")
public class PostDetailResDto {

    @Schema(description = "게시글 ID", example = "10")
    private Long postId;

    @Schema(description = "게시글 제목", example = "맛있는 김치찌개 만드는 법")
    private String title;

    @Schema(description = "요리 ID", example = "5")
    private Long cookId;

    @Schema(description = "요리 이름", example = "김치찌개")
    private String cookName;

    @Schema(description = "작성자 닉네임", example = "소복이")
    private String nickname;

    @Schema(description = "작성자 사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "작성자 권한 ID", example = "123")
    private Long authId;

    @Schema(description = "좋아요 수", example = "15")
    private Long likeCount;

    @Schema(description = "게시글 이미지 목록", example = "[\"https://example.com/image1.jpg\", \"https://example.com/image2.jpg\"]")
    private List<String> images;

    @Schema(description = "게시글 수정 일시", example = "2025-08-05T12:00:00")
    private LocalDateTime updatedAt;

    @Schema(description = "게시글 내용", example = "이곳에 게시글 상세 내용이 들어갑니다.")
    private String content;

    @JsonProperty("baseIngredients")
    @Schema(description = "기본 재료 목록")
    private List<IngredientResDto> defaultIngredients;

    @JsonProperty("additionalIngredients")
    @Schema(description = "추가 재료 목록")
    private List<IngredientResDto> extraIngredients;
}