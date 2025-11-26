package com.imran.edcassistant.model.dto;

import lombok.Data;

@Data
public class ValidationResponse {
    private String assetId;
    private boolean discoverable;
    private boolean policyAttached;
    private String status;
    private String message;
}
