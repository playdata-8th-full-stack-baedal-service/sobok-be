package com.sobok.postservice.post.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostRegisterResDto {
    private Long postId;
    private String cookName;
}