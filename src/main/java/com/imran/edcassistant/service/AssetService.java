package com.imran.edcassistant.service;

import com.imran.edcassistant.model.dto.AssetListResponse;
import com.imran.edcassistant.model.dto.AssetRequestDto;
import com.imran.edcassistant.model.dto.AssetResponseDto;
import com.imran.edcassistant.model.edc.EdcAssetResponse;

public interface AssetService {

    AssetResponseDto createAsset(AssetRequestDto requestDto);

    AssetListResponse getAllAssets();

    EdcAssetResponse getAssetById(String assetId);

}
