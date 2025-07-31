package com.sobok.postservice.post.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostDetailResDto {

    private Long postId;
    private String title;
    private Long cookId;
    private String cookName;
    private String nickname;
    private Long userId;
    private Long authId;
    private Long likeCount;
    private List<String> images;
    private LocalDateTime updatedAt;
    private String content;

    @JsonProperty("baseIngredients") // json에서 써있는 이름으로 표시
    private List<IngredientResDto> defaultIngredients;

    @JsonProperty("additionalIngredients")
    private List<IngredientResDto> extraIngredients;
}