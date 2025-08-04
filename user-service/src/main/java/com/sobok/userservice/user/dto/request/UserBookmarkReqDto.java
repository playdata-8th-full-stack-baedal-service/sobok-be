package com.sobok.userservice.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBookmarkReqDto {
    @Schema(description = "즐겨찾기 등록/해제할 요리 ID", example = "42")
    private Long cookId;
}
