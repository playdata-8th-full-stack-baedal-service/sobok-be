package com.sobok.paymentservice.common.enums;

public enum OrderState {
    ORDER_PENDING,
    ORDER_COMPLETE,
    PREPARING_INGREDIENTS,
    READY_FOR_DELIVERY,
    DELIVERY_ASSIGNED,
    DELIVERING,
    DELIVERY_COMPLETE,
    ;

    public static final String DEFAULT_STR = "ORDER_PENDING";

    public OrderState next() {
        return switch (this) {
            case ORDER_PENDING -> ORDER_COMPLETE;
            case ORDER_COMPLETE -> PREPARING_INGREDIENTS;
            case PREPARING_INGREDIENTS -> READY_FOR_DELIVERY;
            case READY_FOR_DELIVERY -> DELIVERY_ASSIGNED;
            case DELIVERY_ASSIGNED -> DELIVERING;
            case DELIVERING -> DELIVERY_COMPLETE;
            default -> this;
        };
    }

    // 필요하다면
    public OrderState previous() {
        return switch (this) {
            case DELIVERY_COMPLETE -> DELIVERING;
            case DELIVERING -> DELIVERY_ASSIGNED;
            case DELIVERY_ASSIGNED -> READY_FOR_DELIVERY;
            case READY_FOR_DELIVERY -> PREPARING_INGREDIENTS;
            case PREPARING_INGREDIENTS -> ORDER_COMPLETE;
            case ORDER_COMPLETE -> ORDER_PENDING;
            default -> this;
        };
    }
}
