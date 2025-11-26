package com.imran.edcassistant.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class AssetListResponse {
    private List<AssetDetails> assets;
    private int total;
}
