package com.sobok.postservice.post.dto.request;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostImageDto {

    private String imageUrl;
    private int index;

}
