package com.sobok.deliveryservice.delivery.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RiderResDto {

    private Long id;
    private Long authId;
    private String name;
    private String phone;
    private String permissionNumber;
    private String active;
    private String loginId; // 추가 다른 로직 응답 오류시 수정해야함

}
