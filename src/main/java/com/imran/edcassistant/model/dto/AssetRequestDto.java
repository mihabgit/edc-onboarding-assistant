package com.imran.edcassistant.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssetRequestDto {

    @NotBlank(message = "Asset name is required!")
    private String name;

    private String description;

    @NotBlank(message = "Content type is required!")
    private String contentType;

    @NotNull(message = "Data address is required!")
    private DataAddress dataAddress;

    @NotNull(message = "Access policy is required!")
    private AccessPolicy accessPolicy;

}
