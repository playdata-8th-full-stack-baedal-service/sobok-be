package com.sobok.postservice.post.dto.request;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostRegisterReqDto {
    private Long cookId;
    private String title;
    private List<PostImageDto> images;
    private String content;
    private Long paymentId;
}
