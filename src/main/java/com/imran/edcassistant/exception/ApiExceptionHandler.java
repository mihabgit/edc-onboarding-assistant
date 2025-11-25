package com.imran.edcassistant.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(EdcException.class)
    public ResponseEntity<Map<String, Object>> handleEdcException(EdcException ex) {
        return ResponseEntity.status(500).body(
                Map.of(
                        "status", "error",
                        "message", ex.getMessage()
                )
        );
    }
}