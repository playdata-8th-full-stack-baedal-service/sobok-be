package com.sobok.apiservice.common.exception;

import com.sobok.apiservice.common.dto.CommonResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
        return new ResponseEntity<>(CommonResponse.fail(status, "엔티티를 찾을 수 없습니다."), status);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> customExceptionHandler(CustomException e) {
        HttpStatus status = e.status;
        log.error("예외 발생! 메세지 : {}", e.getMessage());
        return new ResponseEntity<>(CommonResponse.fail(status, e.getMessage()), status);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> IOExceptionHandler(IOException e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        log.error("예외 발생! 메세지 : {}", e.getMessage());
        return new ResponseEntity<>(CommonResponse.fail(status, "서버 내부 IO 작업 중 오류가 발생했습니다."), status);
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class, //데이터 유효성 검사 실패
            MissingServletRequestParameterException.class, //해당 파라미터를 미포함
            TypeMismatchException.class  //데이터 타입 변환 실패
    })
    public ResponseEntity<?> handleBadRequest(Exception ex) {
        log.info("잘못된 요청 파라미터 또는 타입 불일치 오류 발생: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(CommonResponse.fail(HttpStatus.NOT_FOUND, "잘못된 요청 또는 리소스가 없습니다."));
    }
}