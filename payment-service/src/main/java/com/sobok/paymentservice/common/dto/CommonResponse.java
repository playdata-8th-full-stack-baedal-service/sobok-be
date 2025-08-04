package com.sobok.paymentservice.common.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Builder
@Getter
public class CommonResponse<T> {
    private final boolean success;
    private final T data;
    private final String message;
    private final int status;

    // ------------------------------------------ 성공 ---------------------------------------------------------
    public static <T> CommonResponse<T> ok(T data) {
        return CommonResponse.<T>builder()
                .success(true)
                .data(data)
                .status(200)
                .message("성공적으로 응답이 전송되었습니다.")
                .build();
    }

    public static <T> CommonResponse<T> ok(T data, HttpStatus status) {
        return CommonResponse.<T>builder()
                .success(true)
                .data(data)
                .status(status.value())
                .message("성공적으로 응답이 전송되었습니다.")
                .build();
    }


    public static <T> CommonResponse<T> ok(T data, String message) {
        return CommonResponse.<T>builder()
                .success(true)
                .data(data)
                .status(200)
                .message(message)
                .build();
    }

    public static <T> ResponseEntity<CommonResponse<T>> response(T data, String message) {
        return ResponseEntity.ok().body(CommonResponse.ok(data, message));
    }

    // ------------------------------------------ 실패 ---------------------------------------------------------
    public static <T> CommonResponse<T> fail(HttpStatus status) {
        return CommonResponse.<T>builder()
                .success(false)
                .message("응답에 실패하였습니다.")
                .status(status.value())
                .build();
    }

    public static <T> CommonResponse<T> fail(HttpStatus status, String message) {
        return CommonResponse.<T>builder()
                .success(false)
                .message(message)
                .status(status.value())
                .build();
    }

    public static <T> CommonResponse<T> fail(int status, String message) {
        return CommonResponse.<T>builder()
                .success(false)
                .message(message)
                .status(status)
                .build();
    }
}
