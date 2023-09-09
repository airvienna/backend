package com.airvienna.demo.user.exception;

import org.springframework.http.HttpStatus;

// refresh token이 유효하지 않음
public class InvalidRefreshTokenException extends RuntimeException {
    private final HttpStatus httpStatus;

    public InvalidRefreshTokenException(String message) {
        super(message);
        this.httpStatus = HttpStatus.UNAUTHORIZED;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}