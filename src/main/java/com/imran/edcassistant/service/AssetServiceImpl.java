package com.imran.edcassistant.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.imran.edcassistant.model.dto.*;
import com.imran.edcassistant.client.EdcClient;
import com.imran.edcassistant.model.edc.EdcAsset;
import com.imran.edcassistant.model.edc.EdcDataAddress;
import com.imran.edcassistant.model.edc.EdcPolicyDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class AssetServiceImpl implements AssetService {

    private final EdcClient  edcClient;
    private final PolicyService policyService;
    private final String catalogUrl;

    public AssetServiceImpl(EdcClient edcClient, PolicyService policyService,@Value("${app.catalog.url:http://localhost:8090/api/catalog}") String catalogUrl) {
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
            EdcPolicyDefinition policy = edcClient.createPolicy(policyDefinition);
            log.info("Created policy: {}", policy);

            EdcAsset edcAsset = buildEdcAsset(assetId, requestDto, policyId);
            EdcAsset asset = edcClient.createAsset(edcAsset);
            log.info("Created asset: {}", asset);

            AssetResponseDto responseDto = new AssetResponseDto();
            responseDto.setAssetId(assetId);
            responseDto.setStatus("published");
            responseDto.setCatalogUrl(catalogUrl);
            responseDto.setMessage("Asset successfully registered and published");

            return responseDto;

        } catch (Exception e) {
            log.error("Error creating asset: {}", requestDto, e);
            throw new  RuntimeException("Asset creation failed", e);
        }

    }

    private EdcAsset buildEdcAsset(String assetId, AssetRequestDto requestDto, String policyId) {
        EdcAsset edcAsset = new EdcAsset();
        edcAsset.setId(assetId);

        Map<String, Object> properties = new HashMap<>();
        properties.put("name", requestDto.getName());
        properties.put("contenttype", requestDto.getContentType());
        properties.put("description", requestDto.getDescription());
        properties.put("policyId", policyId);
        edcAsset.setProperties(properties);

        EdcDataAddress dataAddress = new EdcDataAddress();
        dataAddress.setType("HttpData");
        dataAddress.setBaseUrl(requestDto.getDataAddress().getBaseUrl());
        dataAddress.setMethod("GET");
        dataAddress.setProxyBody(true);
        dataAddress.setProxyMethod(true);
        edcAsset.setEdcDataAddress(dataAddress);

        return edcAsset;

    }

    public AssetListResponse getAllAssets() {
        log.info("Fetching all assets...");

        try {
            List<EdcAsset> edcAssets = edcClient.getAllAssets();
            List<AssetDetails>  assetDetails = new ArrayList<>();
            for (EdcAsset edcAsset : edcAssets) {
                AssetDetails  assetDetail = convertToAssetDetails(edcAsset);
                assetDetails.add(assetDetail);
            }

            AssetListResponse assetListResponse = new AssetListResponse();
            assetListResponse.setAssets(assetDetails);
            assetListResponse.setTotal(edcAssets.size());
            return assetListResponse;

        }  catch (Exception e) {
            log.error("Error fetching all assets", e);
            throw new  RuntimeException("Error fetching all assets", e);
        }

    }

    public AssetDetails getAssetById(String assetId) {
        log.info("Fetching asset by id: {}", assetId);

        try {
            EdcAsset edcAsset = edcClient.getAssetByAssetId(assetId);
            return convertToAssetDetails(edcAsset);
        } catch (Exception e) {
            log.error("Error fetching asset by id: {}", assetId, e);
            throw new  RuntimeException("Error fetching asset by id: " + assetId, e);
        }
    }

    private AssetDetails convertToAssetDetails(EdcAsset asset) {

        if (asset == null) {
            return null;
        }

        Map<String, Object> properties = asset.getProperties();
        if (properties == null) {
            return null;
        }

        AssetDetails assetDetails = new AssetDetails();
        assetDetails.setAssetId(asset.getId());
        assetDetails.setName(asset.getProperties().get("name").toString());
        assetDetails.setDescription(asset.getProperties().get("description").toString());
        assetDetails.setContentType(asset.getProperties().get("contenttype").toString());

        AssetPolicy assetPolicy = new AssetPolicy();
        assetPolicy.setType("restricted");
        assetPolicy.setAllowedCompanies(List.of("BPNL000000000001"));
        assetDetails.setPolicy(assetPolicy);
        assetDetails.setCreatedAt(LocalDateTime.now());

        return assetDetails;
    }

}
