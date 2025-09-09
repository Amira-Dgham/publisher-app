package com.mobelite.e2e.api.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.RequestOptions;
import com.mobelite.e2e.api.models.ApiResponse;
import com.mobelite.e2e.shared.constants.HttpMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ApiClient {

    private final APIRequestContext api;

    // Single ObjectMapper instance for all serialization/deserialization
    private final ObjectMapper mapper;

    // Constructor with mapper initialization
    public ApiClient(APIRequestContext api) {
        this.api = api;
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule()); // support LocalDate, LocalDateTime
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public APIResponse execute(HttpMethod method, String endpoint, RequestOptions options) {
        log.info("Executing {} {}", method, endpoint);

        return switch (method) {
            case GET -> api.get(endpoint, options);
            case POST -> api.post(endpoint, options);
            case PUT -> api.put(endpoint, options);
            case PATCH -> api.patch(endpoint, options);
            case DELETE -> api.delete(endpoint, options);
        };
    }

    public String toJson(Object obj) {
        try {
            // Use the shared mapper, not a new one
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize body to JSON", e);
        }
    }

    public <T> T parseResponse(APIResponse response, TypeReference<T> typeReference) {
        try {
            String responseText = response.text();
            log.debug("Parsing response using TypeReference: {}", responseText);
            return mapper.readValue(responseText, typeReference);
        } catch (Exception e) {
            log.error("Failed to parse API response using TypeReference: {}", e.getMessage());
            throw new RuntimeException("Failed to parse API response", e);
        }
    }
    //todo
    public ApiResponse<?> parseErrorResponse(APIResponse response) {
        try {
            String responseText = response.text();
            log.debug("Parsing error response: {}", responseText);
            return mapper.readValue(responseText, new TypeReference<ApiResponse<?>>() {});
        } catch (Exception e) {
            log.warn("Failed to parse error response, falling back to raw text: {}", e.getMessage());
            ApiResponse<Object> errorResponse = new ApiResponse<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage(response.text());
            return errorResponse;
        }
    }
}