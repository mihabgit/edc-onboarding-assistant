package com.imran.edcassistant.model.edc;

import lombok.Data;

import java.util.List;

@Data
public class EdcPolicy {

    private String type = "Set";
    private List<EdcPermission> permission;

}
