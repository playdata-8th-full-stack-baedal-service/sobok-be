package com.sobok.userservice.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor
@Setter
@NoArgsConstructor
@Builder
@Schema(description = "사용자가 좋아요 등록/해제할 게시글 정보 DTO")
/**
 * 게시글 종아요 응답
 */
public class UserLikeResDto {
    @Schema(description = "게시글 ID", example = "5")
    private Long postId;
}