package com.imran.edcassistant.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class AssetPolicy {
    private String type;
    private List<String> allowedCompanies;
}
