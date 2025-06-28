package com.sobok.deliveryservice.common.exception;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {
    HttpStatus status;

    public CustomException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
