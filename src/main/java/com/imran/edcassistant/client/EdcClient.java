package com.imran.edcassistant.client;

import com.imran.edcassistant.exception.EdcApiException;
import com.imran.edcassistant.model.edc.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class EdcClient {

    private final RestTemplate restTemplate;
    private final String edcApiUrl;

    public EdcClient(RestTemplate restTemplate, @Value("${edc.management.api.url:http://localhost:19193}") String edcApiUrl) {
        this.restTemplate = restTemplate;
        this.edcApiUrl = edcApiUrl;
    }

    public String createAsset(String edcAsset) {
        log.info("Creating asset in EDC:{}", edcAsset);

        String url = this.edcApiUrl + "/management/v3/assets";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(edcAsset, headers);

            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, request, Object.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String asset = response.getBody().toString();
                log.info("Successfully created asset in EDC:{}", asset);
                return asset;
            } else {
                throw new EdcApiException("Failed to create asset in EDC " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Error creating asset in EDC:{}", edcAsset, e);
            throw new EdcApiException("Error creating asset in EDC: " + e.getMessage());
        }

    }

    public PolicyResponse createPolicy(EdcPolicyDefinition edcPolicyDefinition) {
        log.info("Creating policy in EDC:{}", edcPolicyDefinition);

        String url = this.edcApiUrl + "/management/v3/policydefinitions";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<EdcPolicyDefinition> request = new HttpEntity<>(edcPolicyDefinition, headers);

            ResponseEntity<PolicyResponse> response = restTemplate.exchange(url, HttpMethod.POST, request, PolicyResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                PolicyResponse policyResponse = response.getBody();
                log.info("Successfully created policy in EDC:{}", policyResponse);
                return policyResponse;
            } else {
                throw new EdcApiException("Failed to create policy in EDC: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Error creating policy in EDC:{}", edcPolicyDefinition, e);
            throw new EdcApiException("Error creating policy in EDC: {} " + e.getMessage());
        }

    }

    public List<EdcAssetResponse> getAllAssets() {
        log.info("Getting all assets in EDC");

        String url = this.edcApiUrl + "/management/v3/assets/request";

        AssetQuerySpec req = new AssetQuerySpec();
        req.setType("QuerySpec");
        req.setLimit(50);
        req.setOffset(0);
        req.setContext(Map.of("@vocab", "https://w3id.org/edc/v0.0.1/ns/"));

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<AssetQuerySpec> request = new HttpEntity<>(req, headers);

            ResponseEntity<List<EdcAssetResponse>> response = restTemplate.exchange(
                    url, HttpMethod.POST, request, new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<EdcAssetResponse> assets = response.getBody();
                log.info("Successfully getting all assets in EDC");
                return assets;
            } else {
                log.error("Failed to get all assets in EDC");
                return Collections.emptyList();
            }

        } catch (Exception e) {
            log.error("Error getting all assets in EDC: {}", e.getMessage(), e);
            throw new EdcApiException("Error getting all assets from EDC: " + e.getMessage());
        }

    }

    public EdcAssetResponse getAssetByAssetId(String assetId) {
        log.info("Getting asset by ID in EDC:{}", assetId);

        List<EdcAssetResponse> assets = getAllAssets();

        return assets.stream()
                .filter(asset -> assetId.equals(asset.getId()))
                .findFirst()
                .orElseThrow(() -> new EdcApiException("Asset not found in EDC: " + assetId));

    }

    public String checkHealth() {
        try {
            getAllAssets();
            return "up";
        } catch (Exception e) {
            return "down";
        }
    }

    public List<PolicyResponse> getAllPolicies() {
        log.info("Getting all policies in EDC");

        String url = this.edcApiUrl + "/management/v3/policydefinitions/request";

        PolicyQuerySpec querySpec = new PolicyQuerySpec();
        querySpec.setType("QuerySpec");
        querySpec.setLimit(50);
        querySpec.setOffset(0);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<PolicyQuerySpec> request = new HttpEntity<>(querySpec, headers);

            ResponseEntity<List<PolicyResponse>> response = restTemplate.exchange(
                    url, HttpMethod.POST, request, new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<PolicyResponse> policies = response.getBody();
                log.info("Successfully getting all policies in EDC");
                return policies;
            } else {
                log.error("Failed to get all policies in EDC");
                return Collections.emptyList();
            }

        } catch (Exception e) {
            log.error("Error getting all policies in EDC: {}", e.getMessage(), e);
            throw new EdcApiException("Error getting all policies from EDC: " + e.getMessage());
        }

    }

}
