package com.sobok.gatewayservice.common.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Builder
@Getter
public class ApiResponse<T> {
    private final boolean success;
    private final T data;
    private final String message;
    private final int code;

    // ------------------------------------------ 성공 ---------------------------------------------------------
    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message("성공적으로 응답이 전송되었습니다.")
                .build();
    }

    public static <T> ApiResponse<T> ok(T data, HttpStatus status) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .code(status.value())
                .message("성공적으로 응답이 전송되었습니다.")
                .build();
    }


    public static <T> ApiResponse<T> ok(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .build();
    }

    // ------------------------------------------ 실패 ---------------------------------------------------------
    public static <T> ApiResponse<T> fail(HttpStatus status) {
        return ApiResponse.<T>builder()
                .success(false)
                .message("응답에 실패하였습니다.")
                .code(status.value())
                .build();
    }

    public static <T> ApiResponse<T> fail(HttpStatus status, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .code(status.value())
                .build();
    }

    public static <T> ApiResponse<T> fail(int status, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .code(status)
                .build();
    }
}
