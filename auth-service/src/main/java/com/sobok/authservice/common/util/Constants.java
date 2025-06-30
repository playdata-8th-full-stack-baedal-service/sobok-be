package com.sobok.authservice.common.util;

public class Constants {
    // 복구 기한
    public static final Long RECOVERY_DAY = 15L;


    // REDIS 키
    public static final String RECOVERY_KEY = "RECOVERY:";
    public static final String REFRESH_TOKEN_KEY = "REFRESH_TOKEN:";


    // RabbitMQ
    // EXCHANGE
    public static final String AUTH_EXCHANGE = "auth.exchange";
    // QUEUE
    public static final String USER_QUEUE = "user.queue";
    public static final String DELIVERY_QUEUE = "delivery.queue";
    public static final String SHOP_QUEUE = "shop.queue";
    // ROUTING_KEY
    public static final String USER_SIGNUP_ROUTING_KEY = "auth.user.signup";
    public static final String RIDER_SIGNUP_ROUTING_KEY = "auth.delivery.signup";
    public static final String HUB_SIGNUP_ROUTING_KEY = "auth.shop.signup";
}
