package com.sobok.postservice.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "게시물 등록 여부 응답 DTO")
public class PostRegisterCheckResDto {
    @Schema(description = "게시물이 등록되었는지 여부", example = "true")
    private boolean isRegistered;
}