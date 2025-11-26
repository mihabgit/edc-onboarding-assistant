package com.imran.edcassistant.model.edc;

import lombok.Data;

@Data
public class EdcDataAddress {

    private String type = "DataAddress";
    private String baseUrl;
    private String method = "GET";
    private boolean proxyBody = true;
    private boolean proxyMethod = true;

}
