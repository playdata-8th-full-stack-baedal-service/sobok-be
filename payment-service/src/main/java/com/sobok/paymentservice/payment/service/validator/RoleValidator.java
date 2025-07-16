package com.sobok.paymentservice.payment.service.validator;

import com.sobok.paymentservice.common.dto.TokenUserInfo;
import com.sobok.paymentservice.common.enums.OrderState;
import com.sobok.paymentservice.common.exception.CustomException;
import com.sobok.paymentservice.payment.dto.delivery.DeliveryResDto;

import java.util.List;

public interface RoleValidator {
    boolean supports(String role); // 어떤 역할을 처리할 수 있는가?
    void validate(TokenUserInfo userInfo, DeliveryResDto delivery) throws CustomException;
    List<OrderState> allowedStates();
}
