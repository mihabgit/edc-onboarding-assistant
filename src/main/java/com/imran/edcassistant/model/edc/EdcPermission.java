package com.imran.edcassistant.model.edc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EdcPermission {

    @JsonProperty("odrl:action")
    public String action;

    @JsonProperty("odrl:constraint")
    private EdcConstraint constraint;

}
