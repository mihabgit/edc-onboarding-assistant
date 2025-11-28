package com.imran.edcassistant.service;

import com.imran.edcassistant.model.dto.Asset;
import com.imran.edcassistant.model.dto.AssetListResponse;
import com.imran.edcassistant.model.dto.AssetRequestDto;
import com.imran.edcassistant.model.dto.AssetResponseDto;

public interface AssetService {

    AssetResponseDto createAsset(AssetRequestDto requestDto);

    AssetListResponse getAllAssets(Integer page, Integer limit, String assetId);

    Asset getAssetById(String assetId);

}
