package com.mobelite.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import com.mobelite.config.ConfigManager;
import com.mobelite.models.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Slf4j
public class ApiUtils {
    private final APIRequestContext api;
    private static ConfigManager config = null;

    public ApiUtils(APIRequestContext api) {
        this.api = api;
        config = ConfigManager.getInstance();
    }

    // Generic JSON parser
    public <T> T parseResponse(APIResponse response, TypeReference<T> typeReference) {
        String responseText = getResponseText(response);
        try {
            return config.getObjectMapper().readValue(responseText, typeReference);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse API response", e);
        }
    }

    public static String getResponseText(APIResponse response) {
        try {
            return response.text();
        } catch (Exception e) {
            log.warn("Failed to read response text: {}", e.getMessage(), e);
            return "";
        }
    }

    /**
     * Builds a Playwright RequestOptions object with optional body and headers
     */
    public static RequestOptions buildRequestOptions(Object body, Map<String, String> headers) {
        RequestOptions options = RequestOptions.create();
        if (headers != null) {
            headers.forEach(options::setHeader);
        }
        if (body != null) {
            options.setData(toJson(body));
        }
        return options;
    }

    public static String toJson(Object obj) {
        try {
            return config.getObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize object to JSON", e);
        }
    }


    public <T> T post(String endpoint, Object requestBody, TypeReference<T> typeReference) {
        try {
            // Send POST request
            APIResponse response = api.post(endpoint,buildRequestOptions(requestBody, null) );

            if (!response.ok()) {
                throw new RuntimeException("POST request failed: " + endpoint +
                        " | status: " + response.status() + " | body: " + getResponseText(response));
            }

            // Parse response
            return parseResponse(response, typeReference);
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute POST request: " + endpoint, e);
        }
    }

    // Generic delete by ID
    public void deleteById(String endpoint, Long id) {
        APIResponse response = api.delete(endpoint + "/" + id);
        response.ok();
    }

    public <T, D> Long getIdByName(
            String endpoint,
            String name,
            TypeReference<ApiResponse<D>> typeRef,
            Function<D, List<T>> contentExtractor,
            Function<T, Long> idExtractor
    ) {
        APIResponse response = api.get(endpoint + "?name=" + name);

        if (!response.ok()) {
            throw new RuntimeException("Failed to fetch entity by name: " + name +
                    " | status: " + response.status() + " | body: " + getResponseText(response));
        }

        try {
            ApiResponse<D> apiResponse = parseResponse(response, typeRef);

            if (apiResponse != null && apiResponse.getData() != null) {
                List<T> content = contentExtractor.apply(apiResponse.getData());
                if (content != null && !content.isEmpty()) {
                    return idExtractor.apply(content.get(0));
                }
            }

            throw new RuntimeException("No entity found with name: " + name);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse response: " + getResponseText(response), e);
        }
    }
}