package com.imran.edcassistant.model.edc;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class EdcPolicyDefinition extends EdcContext {
    private String id;
    private EdcPolicy policy;
}
