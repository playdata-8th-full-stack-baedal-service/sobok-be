package com.sobok.shopservice.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Builder
@Getter
@Schema(description = "공통 응답 포맷")
public class CommonResponse<T> {
    @Schema(description = "요청 성공 여부", example = "true")
    private final boolean success;
    @Schema(description = "응답 데이터", nullable = true)
    private final T data;
    @Schema(description = "응답 메시지", example = "요청이 성공적으로 처리되었습니다.")
    private final String message;
    @Schema(description = "HTTP 상태 코드", example = "200")
    private final int status;

    // ------------------------------------------ 성공 ---------------------------------------------------------
    public static <T> CommonResponse<T> ok(T data) {
        return CommonResponse.<T>builder()
                .success(true)
                .status(200)
                .data(data)
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
