package com.imran.edcassistant.model.edc;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class EdcAsset extends EdcContext {
    private String id;
    private Map<String, Object> properties;
    private EdcDataAddress edcDataAddress;
}
