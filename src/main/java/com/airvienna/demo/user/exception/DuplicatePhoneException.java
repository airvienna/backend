package com.airvienna.demo.user.exception;

import org.springframework.http.HttpStatus;

public class DuplicatePhoneException extends RuntimeException {
    private final HttpStatus httpStatus;

    public DuplicatePhoneException(String message) {
        super(message);
        this.httpStatus = HttpStatus.CONFLICT;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}