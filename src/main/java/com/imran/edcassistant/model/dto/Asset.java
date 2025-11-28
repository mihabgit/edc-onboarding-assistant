package com.imran.edcassistant.model.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class Asset {

    private String assetId;
    private String name;
    private String description;
    private String contentType;
    private Policy policy;
    private Instant createdAt;

}
