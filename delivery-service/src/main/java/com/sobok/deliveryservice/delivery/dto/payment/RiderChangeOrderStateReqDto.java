package com.sobok.deliveryservice.delivery.dto.payment;

import com.sobok.deliveryservice.common.dto.TokenUserInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RiderChangeOrderStateReqDto {
    TokenUserInfo userInfo;
    Long paymentId;;
}
