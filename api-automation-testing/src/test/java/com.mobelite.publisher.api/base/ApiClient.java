package com.mobelite.publisher.api.base;

import com.fasterxml.jackson.core.type.TypeReference;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import com.mobelite.publisher.api.config.ConfigManager;

import com.mobelite.publisher.api.constants.HttpMethod;
import com.mobelite.publisher.api.models.response.ApiResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import static com.mobelite.publisher.api.utils.ApiUtils.getResponseText;

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

        APIResponse response = switch (method) {
            case GET -> api.get(endpoint, options);
            case POST -> api.post(endpoint, options);
            case PUT -> api.put(endpoint, options);
            case PATCH -> api.patch(endpoint, options);
            case DELETE -> api.delete(endpoint, options);
        };

        // Automatically update BaseTest's lastRequest/lastResponse
        test.setLastRequest(String.format("%s %s%nRequest: %s", method, endpoint));
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
        String responseText = getResponseText(response);
        try {
            return config.getObjectMapper().readValue(responseText, typeReference);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse API response", e);
        }
    }

    public ApiResponse<?> parseErrorResponse(APIResponse response) {
        String responseText = getResponseText(response);
        try {
            return config.getObjectMapper().readValue(responseText, new TypeReference<ApiResponse<?>>() {});
        } catch (Exception e) {
            log.warn("Failed to parse error response, falling back to raw text: {}", e.getMessage());
            ApiResponse<Object> errorResponse = new ApiResponse<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage(responseText);
            return errorResponse;
        }
    }
}