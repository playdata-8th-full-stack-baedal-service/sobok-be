package com.sobok.userservice.user.dto.response;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
/**
 * 관리자 전용 전체 주문 조회용(사용자 정보)
 */
public class UserInfoResDto {
    private String nickname;
    private String address;
    private String roadFull;
    private String phone;
}