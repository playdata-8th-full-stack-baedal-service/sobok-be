package com.sobok.postservice.post.dto.request;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostRegisterReqDto {
    private Long paymentId;
    private List<PostUnitDto> posts;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PostUnitDto {
        private Long cookId;
        private String title;
        private String content;
        private List<PostImageDto> images;
    }

}
