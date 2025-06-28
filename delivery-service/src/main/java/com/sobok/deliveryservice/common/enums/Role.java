package com.sobok.deliveryservice.common.enums;

public enum Role {
    USER, ADMIN, HUB, RIDER;

    public static Role from(String role) {
        return Role.valueOf(role.toUpperCase());
    }
}
