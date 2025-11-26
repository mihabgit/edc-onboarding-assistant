package com.imran.edcassistant.model.edc;

import lombok.Data;

import java.util.Map;

@Data
public class EdcContext {

    private Map<String, String> context = Map.of("@vocab", "https://w3id.org/edc/v0.0.1/ns");

}
