package com.sobok.postservice.post.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostRegisterResDto {
    private List<PostInfo> posts;

    @Getter
    @Builder
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PostInfo {
        private Long postId;
        private String cookName;
    }

}