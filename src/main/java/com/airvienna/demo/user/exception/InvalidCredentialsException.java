package com.airvienna.demo.user.exception;

import org.springframework.http.HttpStatus;

// 비밀번호가 일치하지 않음
public class InvalidCredentialsException extends RuntimeException {
    private final HttpStatus httpStatus;

    public InvalidCredentialsException(String message) {
        super(message);
        this.httpStatus = HttpStatus.UNAUTHORIZED;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}