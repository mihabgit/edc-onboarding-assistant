package com.imran.edcassistant.model.dto;

import lombok.Data;

@Data
public class AssetResponseDto {

    private String assetId;
    private String status;
    private String catalogUrl;
    private String message;

}
