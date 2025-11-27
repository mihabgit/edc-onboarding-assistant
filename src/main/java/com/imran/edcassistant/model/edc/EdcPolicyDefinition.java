package com.imran.edcassistant.model.edc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class EdcPolicyDefinition {

    @JsonProperty("@context")
    private Map<String, String> context = Map.of(
            "@vocab", "https://w3id.org/edc/v0.0.1/ns/",
            "odrl",   "http://www.w3.org/ns/odrl/2/"
    );

    @JsonProperty("@id")
    private String id;

    @JsonProperty("policy")
    private EdcPolicy policy;

}
