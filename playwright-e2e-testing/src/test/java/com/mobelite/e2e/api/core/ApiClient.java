package com.mobelite.e2e.api.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.RequestOptions;
import com.mobelite.e2e.api.models.response.ApiResponse;
import com.mobelite.e2e.shared.helpers.PlaywrightSchemaValidator;
import com.mobelite.e2e.shared.constants.HttpMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.mobelite.e2e.shared.helpers.ApiUtils.getResponseText;

@Slf4j
@RequiredArgsConstructor
public class ApiClient {

    private final APIRequestContext api;
    private final ObjectMapper mapper;

    public ApiClient(APIRequestContext api) {
        this.api = api;
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule()); // support LocalDate, LocalDateTime
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    // ---- Execute HTTP requests ----
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

    // ---- Serialize object to JSON ----
    public String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize body to JSON", e);
        }
    }

    // ---- Parse API response ----
    public <T> T parseResponse(APIResponse response, TypeReference<T> typeReference) {
        String responseText = getResponseText(response);
        try {
            log.debug("Parsing response using TypeReference: {}", responseText);
            return mapper.readValue(responseText, typeReference);
        } catch (Exception e) {
            log.error("Failed to parse API response using TypeReference: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to parse API response", e);
        }
    }

    // ---- Parse error response safely ----
    public ApiResponse<?> parseErrorResponse(APIResponse response) {
        String responseText = getResponseText(response);
        try {
            return mapper.readValue(responseText, new TypeReference<ApiResponse<?>>() {});
        } catch (Exception e) {
            log.warn("Failed to parse error response, falling back to raw text: {}", e.getMessage());
            ApiResponse<Object> errorResponse = new ApiResponse<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage(responseText);
            return errorResponse;
        }
    }

    // ---- Fluent execution + parse + schema validation ----
    public <T> ApiResponse<T> executeAndValidate(
            ApiRequestBuilder builder,
            TypeReference<ApiResponse<T>> typeReference,
            String responseSchemaPath,
            String dataSchemaPath,
            String contentSchemaPath,
            int expectedStatus
    ) {
        // Execute the request
        APIResponse rawResponse = builder.execute();

        ApiAssertions.assertStatus(rawResponse, expectedStatus);

        // Parse the response safely
        ApiResponse<T> parsedResponse = parseResponse(rawResponse, typeReference);

        // Validate schema if provided
        if (responseSchemaPath != null) {
            PlaywrightSchemaValidator.validateResponseAndData(
                    parsedResponse,
                    responseSchemaPath,
                    dataSchemaPath,
                    contentSchemaPath
            );
        }

        return parsedResponse;
    }

}