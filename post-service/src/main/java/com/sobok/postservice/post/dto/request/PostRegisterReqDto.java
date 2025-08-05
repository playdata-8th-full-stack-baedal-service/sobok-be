package com.sobok.postservice.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "게시물 등록 요청 DTO")
public class PostRegisterReqDto {

    @Schema(description = "결제 ID", example = "102")
    private Long paymentId;

    @Schema(description = "요리 ID", example = "5")
    private Long cookId;

    @Schema(description = "게시물 제목", example = "김치찌개 맛있게 먹는 법")
    private String title;

    @Schema(description = "게시물 내용", example = "<p>묵은지 김치찌개 삼겹살 식당 스타일</p>")
    private String content;

    @Schema(description = "게시물 이미지 목록")
    private List<PostImageDto> images;

}
