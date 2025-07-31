package com.sobok.cookservice.common.enums;

public enum Role {
    USER, ADMIN, HUB, RIDER, FEIGN;

    public static Role from(String role) {
        return Role.valueOf(role.toUpperCase());
    }
}
