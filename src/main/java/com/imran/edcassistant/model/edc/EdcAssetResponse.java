package com.imran.edcassistant.model.edc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class EdcAssetResponse {

    @JsonProperty("@id")
    private String id;

    @JsonProperty("@type")
    private String type;

    private Map<String, Object> properties;

    @JsonProperty("dataAddress")
    private EdcDataAddressResponse dataAddress;

    @JsonProperty("@context")
    private Map<String, Object> context;

}
