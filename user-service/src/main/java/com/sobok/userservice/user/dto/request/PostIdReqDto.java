package com.sobok.userservice.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * 게시글 좋아요 등록, 해제
 */
public class PostIdReqDto {
    @Schema(description = "게시글 좋아요 등록/해제할 요리 ID", example = "7")
    private Long postId;
}
