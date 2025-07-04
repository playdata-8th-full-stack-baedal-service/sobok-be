package com.sobok.paymentservice.common.enums;

public enum OrderState {
    ORDER_COMPLETE,
    PREPARING_INGREDIENTS,
    READY_FOR_DELIVERY,
    DELIVERY_ASSIGNED,
    DELIVERING,
    DELIVERY_COMPLETE,
    ;

    public static final String DEFAULT_STR = "ORDER_COMPLETE";
}
