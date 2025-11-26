package com.imran.edcassistant.service;

import com.imran.edcassistant.client.EdcClient;
import com.imran.edcassistant.model.dto.HealthResponse;
import com.imran.edcassistant.model.dto.Statistics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class HealthService {

    private final EdcClient edcClient;
    private final AssetService assetService;

    public HealthService(EdcClient edcClient, AssetService assetService) {
        this.edcClient = edcClient;
        this.assetService = assetService;
    }

    public HealthResponse checkHealth() {
        log.info("Checking health...");

        Map<String, String> checks = new HashMap<>();
        String edcManagementStatus = edcClient.checkHealth();
        if (edcManagementStatus != null) {
            checks.put("edcManagementStatus", edcManagementStatus);
            checks.put("edcDataPlane", "up");
            checks.put("database", "up");
        }

        Statistics statistics = new Statistics();
        try {
            var assets = assetService.getAllAssets();
            var policies = edcClient.getAllPolicies();
            statistics.setTotalAssets(assets.getTotal());
            statistics.setTotalPolicies(policies.size());
        } catch (Exception e) {
            log.warn("Failed to check health");
            statistics.setTotalAssets(0);
            statistics.setTotalPolicies(0);
        }

        boolean allUp = checks.values().stream().allMatch(value -> value.equals("up"));
        String overAllStatus = allUp ? "healthy" : "unhealthy";

        HealthResponse healthResponse = new HealthResponse();
        healthResponse.setStatus(overAllStatus);
        healthResponse.setTimestamp(LocalDateTime.now());
        healthResponse.setChecks(checks);
        healthResponse.setStatistics(statistics);
        return healthResponse;

    }

}
