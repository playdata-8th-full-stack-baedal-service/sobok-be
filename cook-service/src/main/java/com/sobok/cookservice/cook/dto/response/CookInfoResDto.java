package com.sobok.cookservice.cook.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 요리 정보
public class CookInfoResDto {
    private Long cookId;
    private String name;
    private String thumbnail;
    private String active;
}
