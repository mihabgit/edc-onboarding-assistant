package com.imran.edcassistant.service;

import com.imran.edcassistant.model.dto.AssetDetails;
import com.imran.edcassistant.model.dto.AssetListResponse;
import com.imran.edcassistant.model.dto.AssetRequestDto;
import com.imran.edcassistant.model.dto.AssetResponseDto;

public interface AssetService {

    AssetResponseDto createAsset(AssetRequestDto requestDto);

    AssetListResponse getAllAssets();

    AssetDetails getAssetById(String assetId);

}
