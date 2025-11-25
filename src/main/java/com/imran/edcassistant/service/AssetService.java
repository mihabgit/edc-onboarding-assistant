package com.imran.edcassistant.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.imran.edcassistant.model.AssetCreateRequestDto;

import java.util.Map;

public interface AssetService {

    Map<String, Object> createAsset(AssetCreateRequestDto requestDto) throws JsonProcessingException;

}
