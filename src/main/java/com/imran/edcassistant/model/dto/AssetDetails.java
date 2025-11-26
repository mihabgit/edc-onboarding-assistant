package com.imran.edcassistant.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssetDetails {

    private String assetId;
    private String name;
    private String description;
    private String contentType;
    private AssetPolicy policy;
    private LocalDateTime createdAt;

}
