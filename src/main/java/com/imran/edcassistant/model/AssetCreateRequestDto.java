package com.imran.edcassistant.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssetCreateRequestDto {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotBlank
    private String contentType;

    @NotNull
    private DataAddress dataAddress;

    @NotNull
    private AccessPolicy accessPolicy;

}
