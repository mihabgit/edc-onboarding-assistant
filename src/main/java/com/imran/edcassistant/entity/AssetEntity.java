package com.imran.edcassistant.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "assets")
@Data
public class AssetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String assetId;
    private String name;
    private String description;
    private String contentType;
    private String type;
    private List<String> allowedCompanies;
    private Instant createdAt;
}
