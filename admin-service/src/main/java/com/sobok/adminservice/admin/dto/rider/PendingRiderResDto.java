package com.sobok.adminservice.admin.dto.rider;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder

/*
  라이더 정보
 */
public class PendingRiderResDto {
    private Long authId;
    private String name;
    private String phone;
    private String permissionNumber;
    private String active;
    private String loginId;
}
