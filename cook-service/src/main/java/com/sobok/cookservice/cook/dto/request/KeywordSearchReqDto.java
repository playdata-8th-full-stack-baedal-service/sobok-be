package com.sobok.cookservice.cook.dto.request;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KeywordSearchReqDto {
    private String keyword;
}
