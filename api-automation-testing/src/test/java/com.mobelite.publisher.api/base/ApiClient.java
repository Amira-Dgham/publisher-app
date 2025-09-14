package com.mobelite.publisher.api.base;

import com.fasterxml.jackson.core.type.TypeReference;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import com.mobelite.publisher.api.config.ConfigManager;

import com.mobelite.publisher.api.constants.HttpMethod;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class ApiClient {

    private final APIRequestContext api;
    private final ConfigManager config;
    private final BaseTest test; // reference to BaseTest


    public ApiClient(APIRequestContext api, BaseTest test) {
        this.api = api;
        this.test = test;
        this.config = ConfigManager.getInstance();
    }

    public APIResponse execute(HttpMethod method, String endpoint, RequestOptions options) {
        log.info("Executing {} {}", method, endpoint);

        // Build request info
        String requestBody = options != null && options.data() != null ? options.data().toString() : "{}";

        APIResponse response = switch (method) {
            case GET -> api.get(endpoint, options);
            case POST -> api.post(endpoint, options);
            case PUT -> api.put(endpoint, options);
            case PATCH -> api.patch(endpoint, options);
            case DELETE -> api.delete(endpoint, options);
        };

        // Automatically update BaseTest's lastRequest/lastResponse
        test.setLastRequest(String.format("%s %s%nBody: %s", method, endpoint, requestBody));
        try {
            test.setLastResponse(String.format("Status: %d%nBody: %s", response.status(), response.text()));
        } catch (Exception e) {
            test.setLastResponse("Failed to read response body");
        }

        return response;
    }

    public String toJson(Object obj) {
        try {
            return config.getObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize object to JSON", e);
        }
    }

    public <T> T parseResponse(APIResponse response, TypeReference<T> typeReference) {
        try {
            String body = response.text();
            return config.getObjectMapper().readValue(body, typeReference);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse API response", e);
        }
    }

    public <T> T executeAndValidate(
            HttpMethod method,
            String endpoint,
            RequestOptions options,
            TypeReference<T> typeReference,
            String responseSchemaPath
    ) {
        APIResponse response = execute(method, endpoint, options);

        if (responseSchemaPath != null) {
            PlaywrightSchemaValidator.validate(response.text(), responseSchemaPath);
        }

        return parseResponse(response, typeReference);
    }
}