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
public class RiderResDto {
    private Long id;
    private String name;
    private String phone;
    private String permissionNumber;
    private String active;
    private String loginId;
}
