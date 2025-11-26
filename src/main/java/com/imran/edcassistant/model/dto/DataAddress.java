package com.imran.edcassistant.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class DataAddress {

    @NotBlank(message = "Data address type is required!")
    private String type;

    @NotBlank
    @Pattern(
        regexp = "^https://.*",
        message = "baseUrl must be HTTPS"
    )
    private String baseUrl;

}