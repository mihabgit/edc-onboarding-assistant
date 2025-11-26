package com.imran.edcassistant.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class AccessPolicy {

    @NotEmpty
    private List<String> allowedCompanies;

    @NotBlank
    private String usagePurpose;

}