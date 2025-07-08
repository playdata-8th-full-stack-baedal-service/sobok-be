package com.sobok.deliveryservice.delivery.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
/**
 * 라이더 이름 조회용 Dto
 */
public class RiderNameResDto {
    private String riderName;
    private Long riderId;
}