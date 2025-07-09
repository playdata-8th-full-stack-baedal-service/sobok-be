package com.sobok.cookservice.cook.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 요리 이름 전달용 (주문조회)
public class CookNameResDto {
    private Long cookId;
    private String cookName;
}
