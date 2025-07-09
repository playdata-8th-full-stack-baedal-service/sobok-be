package com.sobok.adminservice.admin.dto.order;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * 가게 정보 전달
 */
public class AdminShopResDto {
    private String shopName;
    private String shopAddress;
    private String shopPhone;
    private String ownerName;
}
