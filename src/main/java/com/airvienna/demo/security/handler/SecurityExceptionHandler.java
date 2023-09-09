package com.airvienna.demo.security.handler;

import com.airvienna.demo.security.execption.InvalidTokenException;
import com.airvienna.demo.security.execption.TokenExpiredException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class SecurityExceptionHandler {
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<String> handleInvalidTokenException(InvalidTokenException ex) {
        return new ResponseEntity(ex.getMessage(), ex.getHttpStatus());
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<String> handleTokenExpiredException(TokenExpiredException ex) {
        return new ResponseEntity(ex.getMessage(), ex.getHttpStatus());
    }
}
