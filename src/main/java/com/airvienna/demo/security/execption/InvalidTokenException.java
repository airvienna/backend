package com.airvienna.demo.security.execption;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends RuntimeException {
    private final HttpStatus httpStatus;

    public InvalidTokenException(String message) {
        super(message);
        this.httpStatus = HttpStatus.UNAUTHORIZED;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}