package com.imran.edcassistant.model.edc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PolicyQuerySpec {

    @JsonProperty("@type")
    private String type = "QuerySpec";

    private int offset;
    private int limit;

}