package com.sobok.paymentservice.payment.dto.payment;

import com.sobok.paymentservice.common.dto.TokenUserInfo;
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
