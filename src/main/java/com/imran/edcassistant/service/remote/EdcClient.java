package com.imran.edcassistant.service.remote;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class EdcClient {

    private static final String BASE_URL = "http://localhost:8181/management";

    public void createAsset(Object body) throws JsonProcessingException {
        callEdcClient("/v3/assets", body);
    }

    public void createPolicy(Object body) throws JsonProcessingException {
        callEdcClient("/v2/policydefinitions", body);
    }

    private void callEdcClient(String path, Object body) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        String json = new ObjectMapper().writeValueAsString(body);

        HttpEntity<Object> entity = new HttpEntity<>(json, httpHeaders);

        var response = restTemplate.exchange(BASE_URL + path, HttpMethod.POST, entity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException(
                    "EDC returned error: " + response.getStatusCode());
        }

    }



}
