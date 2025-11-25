package com.imran.edcassistant.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class DataAddress {

    @NotBlank
    private String type;

    @NotBlank
    @Pattern(
        regexp = "^https://.*",
        message = "baseUrl must be HTTPS"
    )
    private String baseUrl;
}