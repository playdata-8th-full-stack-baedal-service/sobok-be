package com.sobok.paymentservice.payment.service.validator.deliveryAction;

import com.sobok.paymentservice.common.enums.DeliveryState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeliveryActionStrategyFactory {

    private final AssignDeliveryAction assignDeliveryAction;
    private final CompleteDeliveryAction completeDeliveryAction;

    public DeliveryActionHandler getStrategy(DeliveryState state) {
        return switch (state) {
            case ASSIGN  -> assignDeliveryAction;
            case COMPLETE -> completeDeliveryAction;
        };
    }
}
