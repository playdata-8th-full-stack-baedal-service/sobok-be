package com.sobok.adminservice.admin.dto.order;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
  유저 정보 응답
 */
public class UserInfoResDto {
    private String nickname;
    private String address;
    private String roadFull;
    private String phone;
}