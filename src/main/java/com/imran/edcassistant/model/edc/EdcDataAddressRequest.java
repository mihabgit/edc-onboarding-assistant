package com.imran.edcassistant.model.edc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EdcDataAddressRequest {

    @JsonProperty("@type")
    private String typeAnnotation;
    @JsonProperty("https://w3id.org/edc/v0.0.1/ns/type")
    private String type;
    @JsonProperty("https://w3id.org/edc/v0.0.1/ns/baseUrl")
    private String baseUrl;

}
