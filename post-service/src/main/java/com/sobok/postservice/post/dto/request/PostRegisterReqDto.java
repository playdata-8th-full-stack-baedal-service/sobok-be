package com.sobok.postservice.post.dto.request;

import lombok.*;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostRegisterReqDto {
    private Long paymentId;
    private Long cookId;
    private String title;
    private String content;
    private List<PostImageDto> images;

}
