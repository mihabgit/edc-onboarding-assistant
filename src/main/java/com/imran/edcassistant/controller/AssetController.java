package com.imran.edcassistant.controller;

import com.imran.edcassistant.model.dto.Asset;
import com.imran.edcassistant.model.dto.AssetListResponse;
import com.imran.edcassistant.model.dto.AssetRequestDto;
import com.imran.edcassistant.model.dto.AssetResponseDto;
import com.imran.edcassistant.service.AssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/assets")
@RequiredArgsConstructor
@Tag(name = "Asset Management", description = "APIs for managing EDC Assets.")
public class AssetController {

    private final AssetService assetService;

    @PostMapping
    @Operation(summary = "Register a new asset", description = "Register a new asset in EDC with simplified input format.")
    public ResponseEntity<AssetResponseDto> createAsset(@Valid @RequestBody AssetRequestDto requestDto) {
        log.info("Received asset request: {}", requestDto);
        AssetResponseDto response = assetService.createAsset(requestDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "List all assets", description = "Get a list of all created assets.")
    public ResponseEntity<AssetListResponse> getAllAssets(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "50") Integer limit,
            @RequestParam(required = false) String id
    ) {
        log.info("Retrieving all assets...");
        AssetListResponse response = assetService.getAllAssets(page, limit, id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{assetId}")
    public ResponseEntity<Asset> getAsset(@PathVariable("assetId") String assetId) {
        log.info("GetAssetById API is called with : {}", assetId);
        Asset asset = assetService.getAssetById(assetId);
        return ResponseEntity.ok(asset);
    }



}
