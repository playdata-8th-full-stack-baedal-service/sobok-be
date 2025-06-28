package com.sobok.paymentservice.common.enums;

public enum Role {
    USER, ADMIN, HUB, RIDER;

    public static Role from(String role) {
        return Role.valueOf(role.toUpperCase());
    }
}
