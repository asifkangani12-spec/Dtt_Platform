package com.dtt.common.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.time.format.DateTimeParseException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleEverything(Exception ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(MessageSourceHolder.getMessage("api.error.input"));
    }


    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<Object> handleDateError(DateTimeParseException ex) {

        return ResponseEntity.badRequest()
                .body(MessageSourceHolder.getMessage("api.error.date"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {

        return ResponseEntity.badRequest()
                .body(MessageSourceHolder.getMessage(
                        "api.error.typeMismatch",
                        ex.getName()
                ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleInvalidJson(HttpMessageNotReadableException ex) {

        return ResponseEntity.badRequest()
                .body(MessageSourceHolder.getMessage("api.error.json"));
    }

    @ExceptionHandler(IndexOutOfBoundsException.class)
    public ResponseEntity<Object> handleIndexOutOfBounds(IndexOutOfBoundsException ex) {

        return ResponseEntity.badRequest()
                .body(MessageSourceHolder.getMessage("api.error.index"));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntime(RuntimeException ex) {

        return ResponseEntity.badRequest()
                .body(MessageSourceHolder.getMessage(
                        "api.error.processing",
                        ex.getMessage()
                ));
    }
}