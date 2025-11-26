package com.imran.edcassistant.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse errorResponse = new ErrorResponse(
                "VALIDATION_FAILED",
                "Input validation failed",
                LocalDateTime.now(),
                errors
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(EdcApiException.class)
    public ResponseEntity<ErrorResponse> handleEdcException(EdcApiException ex) {

        log.error("EDC API Error: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(
                "EDC_API_ERROR",
                ex.getMessage(),
                LocalDateTime.now(),
                null);

        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);

    }

    @ExceptionHandler(AssetNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAssetNotFoundException(AssetNotFoundException ex) {
        log.error("Asset Not Found Error: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(
                "ASSET_NOT_FOUND",
                ex.getMessage(),
                LocalDateTime.now(),
                null
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected Error: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred!",
                LocalDateTime.now(),
                null
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);

    }

}