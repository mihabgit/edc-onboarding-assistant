package com.imran.edcassistant.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
public class ErrorResponse {

    private String code;
    private String message;
    private LocalDateTime timestamp;
    private Map<String, String> details;

}
