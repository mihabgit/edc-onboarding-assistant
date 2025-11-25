package com.imran.edcassistant.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.imran.edcassistant.model.AssetCreateRequestDto;
import com.imran.edcassistant.service.AssetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createAsset(@Valid @RequestBody AssetCreateRequestDto requestDto) throws JsonProcessingException {

        return ResponseEntity.ok(assetService.createAsset(requestDto));

    }


}
