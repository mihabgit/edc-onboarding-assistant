package com.imran.edcassistant.model.edc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class AssetQuerySpec {

    @JsonProperty("@context")
    private Map<String, Object> context;

    @JsonProperty("@type")
    private String type;

    private Integer limit;
    private Integer offset;

}
