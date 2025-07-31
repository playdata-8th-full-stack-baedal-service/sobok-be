package com.sobok.shopservice.shop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryAvailShopResDto {
    private Long shopId;
    private String shopName;
    private String roadFull;

    public static List<Long> convertShopIdList(List<DeliveryAvailShopResDto> reqDto) {
        return reqDto.stream()
                .map(DeliveryAvailShopResDto::getShopId)
                .toList();
    }
}
