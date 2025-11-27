package com.imran.edcassistant.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imran.edcassistant.client.EdcClient;
import com.imran.edcassistant.model.dto.AssetListResponse;
import com.imran.edcassistant.model.dto.AssetRequestDto;
import com.imran.edcassistant.model.dto.AssetResponseDto;
import com.imran.edcassistant.model.edc.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class AssetServiceImpl implements AssetService {

    private final EdcClient edcClient;
    private final PolicyService policyService;
    private final String catalogUrl;

    public AssetServiceImpl(EdcClient edcClient, PolicyService policyService,
                            @Value("${app.catalog.url:http://localhost:8090/api/catalog}") String catalogUrl) {
        this.edcClient = edcClient;
        this.policyService = policyService;
        this.catalogUrl = catalogUrl;
    }


    @Override
    public AssetResponseDto createAsset(AssetRequestDto requestDto) {
        log.info("Creating asset: {}", requestDto);

        String assetId = "asset-" + UUID.randomUUID().toString().substring(0, 8);
        String policyId = "policy-" + UUID.randomUUID().toString().substring(0, 8);

        try {
            EdcPolicyDefinition policyDefinition = policyService.createEdcPolicy(policyId, requestDto.getAccessPolicy());
            PolicyResponse policy = edcClient.createPolicy(policyDefinition);
            log.info("Created policy: {}", policy);

            String edcAsset = buildEdcAsset(assetId, requestDto);
            String asset = edcClient.createAsset(edcAsset);
            log.info("Created asset: {}", asset);

            AssetResponseDto responseDto = new AssetResponseDto();
            responseDto.setAssetId(assetId);
            responseDto.setStatus("published");
            responseDto.setCatalogUrl(catalogUrl);
            responseDto.setMessage("Asset successfully registered and published");

            return responseDto;

        } catch (Exception e) {
            log.error("Error creating asset: {}", requestDto, e);
            throw new RuntimeException("Asset creation failed", e);
        }

    }

    private String buildEdcAsset(String assetId, AssetRequestDto requestDto) throws JsonProcessingException {
        EdcAsset edcAsset = new EdcAsset();
        edcAsset.setId(assetId);
        edcAsset.setType("Asset");

        Map<String, Object> properties = new HashMap<>();
        properties.put("name", requestDto.getName());
        edcAsset.setProperties(properties);

        EdcDataAddressRequest dataAddress = new EdcDataAddressRequest();
        dataAddress.setTypeAnnotation("DataAddress");
        dataAddress.setType("HttpData");
        dataAddress.setBaseUrl(requestDto.getDataAddress().getBaseUrl());
        edcAsset.setDataAddress(dataAddress);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(edcAsset);
        log.info("build asset: {}", json);

        return json;

    }

    public AssetListResponse getAllAssets(Integer page, Integer limit, String assetId) {
        log.info("Fetching all assets...");

        try {
            List<EdcAssetResponse> allAssets = edcClient.getAllAssets();

            // Apply search filter in memory
            List<EdcAssetResponse> filtered = allAssets.stream()
                    .filter(asset -> assetId == null || asset.getId().contains(assetId))
                    .toList();

            // Apply pagination in memory
            int start = Math.min(page, filtered.size());
            int end = Math.min(start + limit, filtered.size());
            List<EdcAssetResponse> paginated = filtered.subList(start, end);

            AssetListResponse assetListResponse = new AssetListResponse();
            assetListResponse.setAssets(paginated);
            assetListResponse.setTotal(allAssets.size());
            return assetListResponse;

        } catch (Exception e) {
            log.error("Error fetching all assets", e);
            throw new RuntimeException("Error fetching all assets", e);
        }

    }

    public EdcAssetResponse getAssetById(String assetId) {
        log.info("Fetching asset by id: {}", assetId);

        try {
            return edcClient.getAssetByAssetId(assetId);
        } catch (Exception e) {
            log.error("Error fetching asset by id: {}", assetId, e);
            throw new RuntimeException("Error fetching asset by id: " + assetId, e);
        }
    }

}
