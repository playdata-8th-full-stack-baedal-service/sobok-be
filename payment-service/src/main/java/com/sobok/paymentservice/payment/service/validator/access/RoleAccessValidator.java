package com.sobok.paymentservice.payment.service.validator.access;

import com.sobok.paymentservice.common.dto.TokenUserInfo;
import com.sobok.paymentservice.payment.dto.delivery.DeliveryResDto;
import com.sobok.paymentservice.payment.entity.CartCook;

import java.util.List;

public interface RoleAccessValidator {
    String getRole();
    void validate(TokenUserInfo userInfo, List<CartCook> cartCookList, DeliveryResDto delivery);
}
