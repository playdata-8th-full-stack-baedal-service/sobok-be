package com.sobok.userservice.user.dto.request;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBookmarkReqDto {
    private Long userId;
    private Long cookId;
}
