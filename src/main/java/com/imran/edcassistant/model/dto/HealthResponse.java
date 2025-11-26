package com.imran.edcassistant.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class HealthResponse {
    private String status;
    private LocalDateTime timestamp;
    private Map<String, String> checks;
    private Statistics statistics;
}
