package com.sobok.adminservice.admin.dto.order;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 요리 이름 조회용(주문 조회)
public class CookNameResDto {
    private Long cookId;
    private String cookName;
}
