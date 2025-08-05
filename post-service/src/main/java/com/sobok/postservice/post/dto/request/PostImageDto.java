package com.sobok.postservice.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "게시물 이미지 DTO (게시물 내 이미지 순서 포함)")
public class PostImageDto {

    @Schema(description = "이미지 URL", example = "https://d3c5012dwkvoyc.cloudfront.net/post/e6420278-8f97-4faf-bafa-763660b11c93dan-gold-4_jhDO54BYg-unsplash.jpg")
    private String imageUrl;

    @Schema(description = "이미지 순서 인덱스 (1부터 시작)", example = "1")
    private int index;

}
