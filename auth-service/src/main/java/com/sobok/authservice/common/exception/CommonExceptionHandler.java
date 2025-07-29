package com.sobok.authservice.common.exception;

import com.sobok.authservice.common.dto.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;

@RestControllerAdvice
@Slf4j
public class CommonExceptionHandler {
    // Controller 단에서 발생하는 모든 예외를 일괄 처리하는 클래스
    // 실제 예외는 Service 계층에서 발생하지만, 따로 예외 처리가 없는 경우
    // 메서드를 호출한 상위 계층으로 전파됩니다.

    // 엔터티를 찾지 못했을 때 예외가 발생할 것이고, 이 메서드가 호출될 것이다.
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> entityNotFoundHandler(EntityNotFoundException e) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        log.error("예외 발생! 메세지 : {}", e.getMessage());
        return new ResponseEntity<>(ApiResponse.fail(status, e.getMessage()), status);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> handleIOException(IOException e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        log.error("예외 발생! 메세지 : {}", e.getMessage());
        return new ResponseEntity<>(ApiResponse.fail(status, e.getMessage()), status);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> customExceptionHandler(CustomException e) {
        HttpStatus status = e.status;
        log.error("예외 발생! 메세지 : {}", e.getMessage());
        return new ResponseEntity<>(ApiResponse.fail(status, e.getMessage()), status);
    }

    // 아이디 비밀번호 유호성 검증 예외 핸들러
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((m1, m2) -> m1 + ", " + m2)
                .orElse("입력값이 올바르지 않습니다.");

        return new ResponseEntity<>(ApiResponse.fail(HttpStatus.BAD_REQUEST, "입력 오류: " + message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class, //해당 파라미터를 미포함
            TypeMismatchException.class  //데이터 타입 변환 실패
    })
    public ResponseEntity<?> handleBadRequest(Exception ex) {
        log.info("잘못된 요청 파라미터 또는 타입 불일치 오류 발생: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail(HttpStatus.NOT_FOUND, "잘못된 요청 또는 리소스가 없습니다."));
    }
}