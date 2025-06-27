package com.sobok.authservice.common.enums;

public enum Role {
    USER, ADMIN, SHOP, RIDER;

    public static Role from(String role) {
        return Role.valueOf(role.toUpperCase());
    }
}
