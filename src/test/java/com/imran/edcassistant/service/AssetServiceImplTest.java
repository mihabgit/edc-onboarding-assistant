package com.imran.edcassistant.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.imran.edcassistant.AssetRepository;
import com.imran.edcassistant.client.EdcClient;
import com.imran.edcassistant.model.dto.*;
import com.imran.edcassistant.model.edc.EdcAssetResponse;
import com.imran.edcassistant.model.edc.EdcPolicyDefinition;
import com.imran.edcassistant.model.edc.PolicyResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetServiceImplTest {

    @Mock
    private EdcClient edcClient;

    @Mock
    private PolicyService policyService;

    @Captor
    private ArgumentCaptor<String> assetJsonCaptor;

    private AssetServiceImpl assetService;

    private final String catalogUrl = "http://localhost:8090/api/catalog";

    private AssetRepository assetRepository;

    @BeforeEach
    void setUp() {
        assetService = new AssetServiceImpl(edcClient, policyService, catalogUrl, assetRepository);
    }

    @Test
    void createAsset_Success() throws JsonProcessingException {
        // Given
        AssetRequestDto requestDto = createSampleAssetRequestDto();
        String policyId = "policy-abc123";
        PolicyResponse policyResponse = new PolicyResponse();
        policyResponse.setId(policyId);

        when(policyService.createEdcPolicy(anyString(), any())).thenReturn(new EdcPolicyDefinition());
        when(edcClient.createPolicy(any(EdcPolicyDefinition.class))).thenReturn(policyResponse);
        when(edcClient.createAsset(anyString())).thenReturn("asset-response");

        // When
        AssetResponseDto result = assetService.createAsset(requestDto);

        // Then
        assertNotNull(result);
        assertNotNull(result.getAssetId());
        assertTrue(result.getAssetId().startsWith("asset-"));
        assertEquals("published", result.getStatus());
        assertEquals(catalogUrl, result.getCatalogUrl());
        assertEquals("Asset successfully registered and published", result.getMessage());

        verify(policyService).createEdcPolicy(anyString(), any());
        verify(edcClient).createPolicy(any(EdcPolicyDefinition.class));
        verify(edcClient).createAsset(assetJsonCaptor.capture());

        // Verify the generated asset JSON
        String generatedAssetJson = assetJsonCaptor.getValue();
        assertTrue(generatedAssetJson.contains(requestDto.getName()));
        assertTrue(generatedAssetJson.contains(requestDto.getDataAddress().getBaseUrl()));
    }

    @Test
    void createAsset_PolicyCreationFails_ShouldThrowException() {
        // Given
        AssetRequestDto requestDto = createSampleAssetRequestDto();

        when(policyService.createEdcPolicy(anyString(), any()))
                .thenThrow(new RuntimeException("Policy creation failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> assetService.createAsset(requestDto));

        assertEquals("Asset creation failed", exception.getMessage());
        verify(edcClient, never()).createAsset(anyString());
    }

    @Test
    void createAsset_AssetCreationFails_ShouldThrowException() {
        // Given
        AssetRequestDto requestDto = createSampleAssetRequestDto();

        when(policyService.createEdcPolicy(anyString(), any())).thenReturn(new EdcPolicyDefinition());
        when(edcClient.createPolicy(any(EdcPolicyDefinition.class))).thenReturn(new PolicyResponse());
        when(edcClient.createAsset(anyString())).thenThrow(new RuntimeException("EDC API error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> assetService.createAsset(requestDto));

        assertEquals("Asset creation failed", exception.getMessage());
    }

    @Test
    void getAllAssets_WithoutFilters_ShouldReturnAllAssets() {
        // Given
        List<EdcAssetResponse> mockAssets = List.of(
                createEdcAssetResponse("asset-1", "Test Asset 1"),
                createEdcAssetResponse("asset-2", "Test Asset 2")
        );

        when(edcClient.getAllAssets()).thenReturn(mockAssets);

        // When
        AssetListResponse result = assetService.getAllAssets(0, 10, null);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotal());
        assertEquals(2, result.getAssets().size());
        verify(edcClient).getAllAssets();
    }

    @Test
    void getAllAssets_WithAssetIdFilter_ShouldReturnFilteredAssets() {
        // Given
        List<EdcAssetResponse> mockAssets = List.of(
                createEdcAssetResponse("asset-123", "Test Asset"),
                createEdcAssetResponse("asset-456", "Another Asset"),
                createEdcAssetResponse("other-id", "Different Asset")
        );

        when(edcClient.getAllAssets()).thenReturn(mockAssets);

        // When
        AssetListResponse result = assetService.getAllAssets(0, 10, "asset-");

        // Then
        assertNotNull(result);
        assertEquals(3, result.getTotal()); // Total count includes all assets
        assertEquals(2, result.getAssets().size()); // Filtered count
        assertTrue(result.getAssets().stream().allMatch(asset -> asset.getAssetId().contains("asset-")));
    }

    @Test
    void getAllAssets_WithPagination_ShouldReturnPaginatedResults() {
        // Given
        List<EdcAssetResponse> mockAssets = List.of(
                createEdcAssetResponse("asset-1", "Asset 1"),
                createEdcAssetResponse("asset-2", "Asset 2"),
                createEdcAssetResponse("asset-3", "Asset 3"),
                createEdcAssetResponse("asset-4", "Asset 4")
        );

        when(edcClient.getAllAssets()).thenReturn(mockAssets);

        // When
        AssetListResponse result = assetService.getAllAssets(1, 2, null);

        // Then
        assertNotNull(result);
        assertEquals(4, result.getTotal());
        assertEquals(2, result.getAssets().size());
        assertEquals("asset-2", result.getAssets().get(0).getAssetId());
        assertEquals("asset-3", result.getAssets().get(1).getAssetId());
    }

    @Test
    void getAllAssets_PaginationOutOfBounds_ShouldHandleGracefully() {
        // Given
        List<EdcAssetResponse> mockAssets = List.of(
                createEdcAssetResponse("asset-1", "Asset 1"),
                createEdcAssetResponse("asset-2", "Asset 2")
        );

        when(edcClient.getAllAssets()).thenReturn(mockAssets);

        // When - page beyond available data
        AssetListResponse result = assetService.getAllAssets(5, 10, null);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotal());
        assertTrue(result.getAssets().isEmpty());
    }

    @Test
    void getAllAssets_ClientThrowsException_ShouldPropagate() {
        // Given
        when(edcClient.getAllAssets()).thenThrow(new RuntimeException("EDC API unavailable"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> assetService.getAllAssets(0, 10, null));

        assertEquals("Error fetching all assets", exception.getMessage());
    }

    @Test
    void getAssetById_Success() {
        // Given
        String assetId = "asset-123";
        EdcAssetResponse expectedResponse = createEdcAssetResponse(assetId, "Test Asset");

        when(edcClient.getAssetByAssetId(assetId)).thenReturn(expectedResponse);

        // When
        Asset result = assetService.getAssetById(assetId);

        // Then
        assertNotNull(result);
        assertEquals(assetId, result.getAssetId());
        verify(edcClient).getAssetByAssetId(assetId);
    }

    @Test
    void getAssetById_NotFound_ShouldThrowException() {
        // Given
        String assetId = "non-existent-asset";

        when(edcClient.getAssetByAssetId(assetId))
                .thenThrow(new RuntimeException("Asset not found"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> assetService.getAssetById(assetId));

        assertEquals("Error fetching asset by id: " + assetId, exception.getMessage());
    }

    private AssetRequestDto createSampleAssetRequestDto() {
        AssetRequestDto requestDto = new AssetRequestDto();
        requestDto.setName("Test Asset");

        DataAddress dataAddress = new DataAddress();
        dataAddress.setBaseUrl("https://api.example.com/data");
        requestDto.setDataAddress(dataAddress);

        // Assuming AssetRequestDto has accessPolicy field
        // requestDto.setAccessPolicy(new PolicyDefinition());

        return requestDto;
    }

    private EdcAssetResponse createEdcAssetResponse(String id, String name) {
        EdcAssetResponse response = new EdcAssetResponse();
        response.setId(id);
        response.setProperties(Map.of("name", name));
        return response;
    }
}