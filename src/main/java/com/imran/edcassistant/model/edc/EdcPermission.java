package com.imran.edcassistant.model.edc;

import lombok.Data;

@Data
public class EdcPermission {

    private String action;
    private EdcConstraint constraint;

}
