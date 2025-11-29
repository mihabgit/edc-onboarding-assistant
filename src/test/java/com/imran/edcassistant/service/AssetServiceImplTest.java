package com.imran.edcassistant.service;

import com.imran.edcassistant.AssetRepository;
import com.imran.edcassistant.client.EdcClient;
import com.imran.edcassistant.entity.AssetEntity;
import com.imran.edcassistant.model.dto.*;
import com.imran.edcassistant.model.edc.EdcAssetResponse;
import com.imran.edcassistant.model.edc.EdcPolicyDefinition;
import com.imran.edcassistant.model.edc.PolicyResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AssetServiceImplTest {

    private EdcClient edcClient;
    private PolicyService policyService;
    private AssetRepository assetRepository;

    private AssetServiceImpl assetService;

    @BeforeEach
    void setup() {
        edcClient = mock(EdcClient.class);
        policyService = mock(PolicyService.class);
        assetRepository = mock(AssetRepository.class);

        assetService = new AssetServiceImpl(
                edcClient,
                policyService,
                "http://test-catalog",
                assetRepository
        );
    }

    @Test
    void testCreateAsset_success() {
        // Prepare DTO
        AssetRequestDto request = new AssetRequestDto();
        request.setName("Test Asset");
        request.setDescription("Desc");
        request.setContentType("application/json");

        AccessPolicy policyDto = new AccessPolicy();
        policyDto.setAllowedCompanies(List.of("ABC"));
        request.setAccessPolicy(policyDto);

        DataAddress da = new DataAddress();
        da.setBaseUrl("http://example.com");
        request.setDataAddress(da);

        // Mock policy creation
        EdcPolicyDefinition policyDefinition = new EdcPolicyDefinition();
        when(policyService.createEdcPolicy(anyString(), any())).thenReturn(policyDefinition);

        PolicyResponse policyResponse = new PolicyResponse();
        policyResponse.setType("USE");
        when(edcClient.createPolicy(any())).thenReturn(policyResponse);

        // Mock asset creation
        when(edcClient.createAsset(anyString())).thenReturn("OK");

        // Run method
        AssetResponseDto response = assetService.createAsset(request);

        // Verify
        assertNotNull(response.getAssetId());
        assertEquals("published", response.getStatus());
        assertEquals("http://test-catalog", response.getCatalogUrl());

        // Verify repository save
        ArgumentCaptor<AssetEntity> captor = ArgumentCaptor.forClass(AssetEntity.class);
        verify(assetRepository).save(captor.capture());
        AssetEntity saved = captor.getValue();

        assertEquals("Test Asset", saved.getName());
        assertEquals("Desc", saved.getDescription());
        assertEquals("application/json", saved.getContentType());
        assertEquals("USE", saved.getType());
        assertEquals(List.of("ABC"), saved.getAllowedCompanies());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void testCreateAsset_failure() {
        AssetRequestDto request = new AssetRequestDto();
        request.setAccessPolicy(new AccessPolicy());
        request.setDataAddress(new DataAddress());

        when(policyService.createEdcPolicy(anyString(), any()))
                .thenThrow(new RuntimeException("Policy creation failed"));

        assertThrows(RuntimeException.class,
                () -> assetService.createAsset(request));
    }

    @Test
    void testGetAllAssets() {
        // Mock EDC asset list
        EdcAssetResponse a1 = new EdcAssetResponse();
        a1.setId("asset-1");

        EdcAssetResponse a2 = new EdcAssetResponse();
        a2.setId("asset-2");

        when(edcClient.getAllAssets()).thenReturn(List.of(a1, a2));

        // Mock repo DB fetch
        AssetEntity e1 = new AssetEntity();
        e1.setAssetId("asset-1");
        e1.setName("A1");

        AssetEntity e2 = new AssetEntity();
        e2.setAssetId("asset-2");
        e2.setName("A2");

        when(assetRepository.findAllByAssetIdIn(List.of("asset-1", "asset-2")))
                .thenReturn(List.of(e1, e2));

        AssetListResponse result = assetService.getAllAssets(0, 10, null);

        assertEquals(2, result.getTotal());
        assertEquals(2, result.getAssets().size());

        assertEquals("A1", result.getAssets().get(0).getName());
        assertEquals("A2", result.getAssets().get(1).getName());
    }

    @Test
    void testGetAllAssets_filterAndPaginate() {
        EdcAssetResponse a1 = new EdcAssetResponse();
        a1.setId("asset-a1");

        EdcAssetResponse a2 = new EdcAssetResponse();
        a2.setId("asset-b2");

        EdcAssetResponse a3 = new EdcAssetResponse();
        a3.setId("asset-c3");

        when(edcClient.getAllAssets()).thenReturn(List.of(a1, a2, a3));

        // Only expecting asset-b2 after filter + pagination
        AssetEntity entity = new AssetEntity();
        entity.setAssetId("asset-b2");
        entity.setName("Filtered Asset");

        when(assetRepository.findAllByAssetIdIn(List.of("asset-b2")))
                .thenReturn(List.of(entity));

        AssetListResponse response =
                assetService.getAllAssets(0, 1, "b2");

        assertEquals(3, response.getTotal());     // total from EDC API
        assertEquals(1, response.getAssets().size());
        assertEquals("Filtered Asset", response.getAssets().get(0).getName());
    }

    @Test
    void testGetAssetById() {
        EdcAssetResponse edcResponse = new EdcAssetResponse();
        edcResponse.setId("asset-123");

        when(edcClient.getAssetByAssetId("asset-123"))
                .thenReturn(edcResponse);

        AssetEntity entity = new AssetEntity();
        entity.setAssetId("asset-123");
        entity.setName("My Asset");
        entity.setType("USE");
        entity.setAllowedCompanies(List.of("X"));
        entity.setCreatedAt(Instant.now());

        when(assetRepository.getAssetByAssetId("asset-123"))
                .thenReturn(entity);

        Asset asset = assetService.getAssetById("asset-123");

        assertEquals("asset-123", asset.getAssetId());
        assertEquals("My Asset", asset.getName());
        assertEquals("USE", asset.getPolicy().getType());
        assertEquals(List.of("X"), asset.getPolicy().getAllowedCompanies());
    }

    @Test
    void testGetAssetById_failure() {
        when(edcClient.getAssetByAssetId("bad-id"))
                .thenThrow(new RuntimeException("Not found"));

        assertThrows(RuntimeException.class,
                () -> assetService.getAssetById("bad-id"));
    }
}
