package com.imran.edcassistant.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.imran.edcassistant.model.AssetCreateRequestDto;
import com.imran.edcassistant.service.remote.EdcClient;
import com.imran.edcassistant.service.remote.EdcPolicyBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {

    private final EdcClient  edcClient;
    private final EdcPolicyBuilder  edcPolicyBuilder;


    @Override
    public Map<String, Object> createAsset(AssetCreateRequestDto requestDto) throws JsonProcessingException {

        String assetId = "asset-" + UUID.randomUUID();
        String policyId = "policy-" + UUID.randomUUID();

        var edcAsset = Map.of(
                "asset", Map.of(
                        "properties", Map.of(
                                "asset:prop:id", assetId,
                                "asset:prop:name", requestDto.getName(),
                                "asset:prop:description", requestDto.getDescription(),
                                "asset:prop:contenttype", requestDto.getContentType()
                        )
                ),
                "dataAddress", Map.of(
                        "properties", Map.of(
                                "type", requestDto.getDataAddress().getType(),
                                "baseUrl", requestDto.getDataAddress().getBaseUrl()
                        )
                )
        );

        // ODRL policy
        var edcPolicy = edcPolicyBuilder.buildPolicy(policyId, assetId, requestDto.getAccessPolicy());

        edcClient.createAsset(edcAsset);
        edcClient.createPolicy(edcPolicy);

        return Map.of(
                "assetId", assetId,
                "status", "published",
                "catalogUrl", "http://localhost:8080/api/catalog",
                "message", "Asset successfully registered and published!"
        );

    }
}
