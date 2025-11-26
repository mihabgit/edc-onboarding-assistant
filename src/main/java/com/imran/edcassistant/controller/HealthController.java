package com.imran.edcassistant.controller;

import com.imran.edcassistant.model.dto.HealthResponse;
import com.imran.edcassistant.service.HealthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "Health Check", description = "APIs for system health monitoring.")
public class HealthController {

    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping
    @Operation(summary = "System health check", description = "Check the health status of EDC connectors and related services.")
    public ResponseEntity<HealthResponse> checkHealth() {
        log.info("Health check started");
        HealthResponse healthResponse = healthService.checkHealth();
        log.info("Health check finished");
        return ResponseEntity.ok(healthResponse);
    }

}
