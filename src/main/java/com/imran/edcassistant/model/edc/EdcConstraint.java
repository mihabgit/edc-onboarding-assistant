package com.imran.edcassistant.model.edc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EdcConstraint {

    @JsonProperty("odrl:leftOperand")
    private String leftOperand;

    @JsonProperty("odrl:operator")
    private String operator;

    @JsonProperty("odrl:rightOperand")
    private String rightOperand;

}
