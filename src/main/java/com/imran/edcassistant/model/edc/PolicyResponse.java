package com.imran.edcassistant.model.edc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class PolicyResponse {

    @JsonProperty("@type")
    private String type;

    @JsonProperty("@id")
    private String id;

    private long createdAt;

    @JsonProperty("@context")
    private Map<String, String> context;

}
