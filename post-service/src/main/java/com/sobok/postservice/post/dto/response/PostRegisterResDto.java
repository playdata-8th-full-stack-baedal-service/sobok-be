package com.sobok.postservice.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "게시물 등록 응답 DTO")
public class PostRegisterResDto {

    @Schema(description = "등록된 게시물 ID", example = "30")
    private Long postId;

    @Schema(description = "요리 이름", example = "김치찌개")
    private String cookName;
}