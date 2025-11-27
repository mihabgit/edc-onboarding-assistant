package com.imran.edcassistant.model.dto;

import com.imran.edcassistant.model.edc.EdcAssetResponse;
import lombok.Data;

import java.util.List;

@Data
public class AssetListResponse {
    private List<EdcAssetResponse> assets;
    private int total;
}
