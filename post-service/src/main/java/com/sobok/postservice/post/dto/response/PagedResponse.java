package com.sobok.postservice.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "페이지 응답 구조")
/**
 * 게시글 조회
 */
public class PagedResponse<T> {

    @Schema(description = "컨텐츠 목록")
    private List<T> content;

    @Schema(description = "현재 페이지 번호", example = "0")
    private int page;

    @Schema(description = "페이지 크기", example = "10")
    private int size;

    @Schema(description = "전체 요소 수", example = "31")
    private long totalElements;

    @Schema(description = "전체 페이지 수", example = "4")
    private int totalPages;

    @Schema(description = "마지막 페이지 여부", example = "false")
    private boolean last;
}
