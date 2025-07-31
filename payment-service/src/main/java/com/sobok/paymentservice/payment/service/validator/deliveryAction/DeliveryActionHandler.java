package com.sobok.paymentservice.payment.service.validator.deliveryAction;

import com.sobok.paymentservice.common.dto.TokenUserInfo;
import com.sobok.paymentservice.payment.dto.delivery.AcceptOrderReqDto;
import com.sobok.paymentservice.payment.entity.Payment;

import java.util.function.Consumer;

public interface DeliveryActionHandler {
    void execute(TokenUserInfo userInfo, Long paymentId, Consumer<AcceptOrderReqDto> deliveryAction, Payment payment);
}
