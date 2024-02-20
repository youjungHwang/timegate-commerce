package com.example.user_service.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler(HttpException.class)
    public ResponseEntity<ExceptionResponse> handleHttpException(HttpException e) {
        Boolean ok = e.isOk();
        HttpStatus httpStatus = e.getHttpStatus();
        String message = e.getMessage();
        return ResponseEntity.status(e.getHttpStatus()).body(
                new ExceptionResponse(
                        ok,
                        message,
                        httpStatus.value()
                ));
    }
}