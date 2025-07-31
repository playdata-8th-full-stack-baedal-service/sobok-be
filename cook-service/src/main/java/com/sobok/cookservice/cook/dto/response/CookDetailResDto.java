package com.sobok.cookservice.cook.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 요리 하나의 상세 정보
public class CookDetailResDto {
    private Long cookId;
    private String name;
    private String thumbnail;
    private String active;
    private List<Long> ingredientIds; // 식재료 id
}
