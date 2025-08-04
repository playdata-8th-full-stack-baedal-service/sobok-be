package com.sobok.userservice.user.dto.response;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
/**
 * 관리자 전용 전체 주문 조회용(사용자 정보)
 */
public class UserInfoResDto {
    private Long authId;
    private String nickname;
    private String address;
    private String roadFull;
    private String phone;
}