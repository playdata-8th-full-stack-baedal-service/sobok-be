package com.sobok.authservice.auth.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseDto<T> { // 공통 응답 부분
    private boolean success;
    private int status;
    private String message;
    private T data;
}