package com.sobok.postservice.post.dto.response;

import lombok.*;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostRegisterResDto {
    private Long postId;
    private String cookName;
}
