package com.sobok.userservice.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사용자가 즐겨찾기한 요리 정보 DTO")
public class UserBookmarkResDto {
//    private Long userId;
    @Schema(description = "요리 ID", example = "5")
    private Long cookId;
    @Schema(description = "요리 이름", example = "김치찌개")
    private String cookName;
    @Schema(description = "요리 이미지 url", example = "https://d3c5012dwkvoyc.cloudfront.net/food/2b73491b-bcdd-4c88-90fb-fc018aced958pexels-vanmalidate-769289.jpg")
    private String thumbnail;
}
