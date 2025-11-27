package com.imran.edcassistant.model.edc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EdcDataAddressResponse {

    @JsonProperty("@type")
    private String typeAnnotation;
    @JsonProperty("type")
    private String type;
    @JsonProperty("baseUrl")
    private String baseUrl;

}
