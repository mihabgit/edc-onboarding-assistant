package com.imran.edcassistant.model.edc;

import lombok.Data;

@Data
public class EdcConstraint {
    private String leftOperand;
    private String operator;
    private String rightOperand;

}
