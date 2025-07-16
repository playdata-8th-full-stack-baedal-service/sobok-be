package com.sobok.paymentservice.payment.service.validator.access;

import com.sobok.paymentservice.common.dto.TokenUserInfo;
import com.sobok.paymentservice.common.exception.CustomException;
import com.sobok.paymentservice.payment.dto.delivery.DeliveryResDto;
import com.sobok.paymentservice.payment.entity.CartCook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component("HUB")
public class HubAccessValidator implements RoleAccessValidator {
    public void validate(TokenUserInfo userInfo, List<CartCook> cartCookList, DeliveryResDto delivery) {
        if (!Objects.equals(delivery.getShopId(), userInfo.getShopId())) {
            throw new CustomException("현재 가게에서는 접근할 수 없습니다.", HttpStatus.FORBIDDEN);
        }
    }
}
