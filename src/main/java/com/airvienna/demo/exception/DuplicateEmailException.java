package com.airvienna.demo.exception;

import org.springframework.http.HttpStatus;

public class DuplicateEmailException extends RuntimeException {
    private final HttpStatus httpStatus;

    public DuplicateEmailException(String message) {
        super(message);
        this.httpStatus = HttpStatus.CONFLICT;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}