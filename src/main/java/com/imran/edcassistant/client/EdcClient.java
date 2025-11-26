package com.imran.edcassistant.client;

import com.imran.edcassistant.exception.EdcApiException;
import com.imran.edcassistant.model.edc.EdcAsset;
import com.imran.edcassistant.model.edc.EdcAssetQuery;
import com.imran.edcassistant.model.edc.EdcPolicyDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class EdcClient {

    private final RestTemplate restTemplate;
    private final String edcApiUrl;

    public EdcClient(RestTemplate restTemplate, @Value("${edc.management.api.url:http://localhost:8181}") String edcApiUrl) {
        this.restTemplate = restTemplate;
        this.edcApiUrl = edcApiUrl;
    }

    public EdcAsset createAsset(EdcAsset edcAsset) {
        log.info("Creating asset in EDC:{}", edcAsset);

        String url = this.edcApiUrl + "/management/v3/assets";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<EdcAsset> request = new HttpEntity<>(edcAsset, headers);

            ResponseEntity<EdcAsset> response = restTemplate.exchange(url, HttpMethod.POST, request, EdcAsset.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                EdcAsset asset = response.getBody();
                log.info("Successfully created asset in EDC:{}", asset.getId());
                return asset;
            } else {
                throw new EdcApiException("Failed to create asset in EDC "+response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Error creating asset in EDC:{}", edcAsset, e);
            throw new EdcApiException("Error creating asset in EDC: "+e.getMessage());
        }

    }

    public EdcPolicyDefinition createPolicy(EdcPolicyDefinition edcPolicyDefinition) {
        log.info("Creating policy in EDC:{}", edcPolicyDefinition);

        String url = this.edcApiUrl + "/management/v3/policydefinitions";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<EdcPolicyDefinition> request = new HttpEntity<>(edcPolicyDefinition, headers);

            ResponseEntity<EdcPolicyDefinition> response = restTemplate.exchange(url, HttpMethod.POST, request, EdcPolicyDefinition.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                EdcPolicyDefinition policy = response.getBody();
                log.info("Successfully created policy in EDC:{}", policy.getId());
                return policy;
            } else {
                throw new EdcApiException("Failed to create policy in EDC: "+response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Error creating policy in EDC:{}", edcPolicyDefinition, e);
            throw new EdcApiException("Error creating policy in EDC: {} " + e.getMessage());
        }

    }

    public List<EdcAsset> getAllAssets() {
        log.info("Getting all assets in EDC");

        String url = this.edcApiUrl + "/management/v3/assets/request";
        EdcAssetQuery query = new EdcAssetQuery();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<EdcAssetQuery> request = new HttpEntity<>(query, headers);

            ResponseEntity<List<EdcAsset>> response = restTemplate.exchange(
                    url, HttpMethod.POST, request, new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<EdcAsset> assets = response.getBody();
                log.info("Successfully getting all assets in EDC");
                return assets;
            }  else {
                log.error("Failed to get all assets in EDC");
                return Collections.emptyList();
            }

        } catch (Exception e) {
            log.error("Error getting all assets in EDC: {}", e.getMessage(), e);
            throw new EdcApiException("Error getting all assets from EDC: " +e.getMessage());
        }

    }

    public EdcAsset getAssetByAssetId(String assetId) {
        log.info("Getting asset by ID in EDC:{}", assetId);

        List<EdcAsset> assets = getAllAssets();

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

    public List<EdcPolicyDefinition> getAllPolicies() {
        log.info("Getting all policies in EDC");

        return new ArrayList<>();
    }

}
