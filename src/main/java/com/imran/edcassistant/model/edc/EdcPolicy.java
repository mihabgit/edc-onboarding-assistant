package com.imran.edcassistant.model.edc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class EdcPolicy {

    @JsonProperty("@type")
    private final String type = "odrl:Set";

    @JsonProperty("permission")
    private List<EdcPermission> permissions;

}
