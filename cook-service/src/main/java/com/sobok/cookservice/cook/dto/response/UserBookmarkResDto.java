package com.sobok.cookservice.cook.dto.response;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBookmarkResDto {
//    private Long userId;
    private Long cookId;
    private String cookName;
    private String thumbnail;
}
