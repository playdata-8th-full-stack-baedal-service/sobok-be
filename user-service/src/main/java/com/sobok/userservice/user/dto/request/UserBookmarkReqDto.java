package com.sobok.userservice.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사용자가 즐겨찾기 등록/해제할 요리 정보 DTO")
public class UserBookmarkReqDto {
    @Schema(description = "즐겨찾기 등록/해제할 요리 ID", example = "42")
    private Long cookId;
}
