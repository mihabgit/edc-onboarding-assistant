package com.imran.edcassistant.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imran.edcassistant.AssetRepository;
import com.imran.edcassistant.client.EdcClient;
import com.imran.edcassistant.entity.AssetEntity;
import com.imran.edcassistant.model.dto.*;
import com.imran.edcassistant.model.edc.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service
public class AssetServiceImpl implements AssetService {

    private final EdcClient edcClient;
    private final PolicyService policyService;
    private final String catalogUrl;
    private final AssetRepository assetRepository;

    public AssetServiceImpl(EdcClient edcClient, PolicyService policyService,
                            @Value("${app.catalog.url:http://localhost:8090/api/catalog}") String catalogUrl, AssetRepository assetRepository) {
        this.edcClient = edcClient;
        this.policyService = policyService;
        this.catalogUrl = catalogUrl;
        this.assetRepository = assetRepository;
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

            AssetEntity assetEntity = new AssetEntity();
            assetEntity.setAssetId(assetId);
            assetEntity.setName(requestDto.getName());
            assetEntity.setDescription(requestDto.getDescription());
            assetEntity.setCreatedAt(Instant.now());
            assetEntity.setContentType(requestDto.getContentType());
            assetEntity.setType(policy.getType());
            assetEntity.setAllowedCompanies(requestDto.getAccessPolicy().getAllowedCompanies());
            assetRepository.save(assetEntity);

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

            List<String> assetIds = paginated.stream().map(EdcAssetResponse::getId).toList();
            var assetEntities = assetRepository.findAllByAssetIdIn(assetIds);

            List<Asset> assets = assetEntities.stream().map(this::mapEntityToDto).toList();

            AssetListResponse assetListResponse = new AssetListResponse();
            assetListResponse.setAssets(assets);
            assetListResponse.setTotal(allAssets.size());
            return assetListResponse;

        } catch (Exception e) {
            log.error("Error fetching all assets", e);
            throw new RuntimeException("Error fetching all assets", e);
        }

    }

    public Asset getAssetById(String assetId) {
        log.info("Fetching asset by id: {}", assetId);

        try {
             EdcAssetResponse response = edcClient.getAssetByAssetId(assetId);
             AssetEntity assetEntity = assetRepository.getAssetByAssetId(response.getId());
             return mapEntityToDto(assetEntity);

        } catch (Exception e) {
            log.error("Error fetching asset by id: {}", assetId, e);
            throw new RuntimeException("Error fetching asset by id: " + assetId, e);
        }
    }

    Asset mapEntityToDto(AssetEntity assetEntity) {
        Asset asset = new Asset();
        asset.setAssetId(assetEntity.getAssetId());
        asset.setName(assetEntity.getName());
        asset.setDescription(assetEntity.getDescription());
        asset.setCreatedAt(assetEntity.getCreatedAt());
        asset.setContentType(assetEntity.getContentType());
        Policy policy = new Policy();
        policy.setType(assetEntity.getType());
        policy.setAllowedCompanies(assetEntity.getAllowedCompanies());
        asset.setPolicy(policy);

        return asset;
    }

}
