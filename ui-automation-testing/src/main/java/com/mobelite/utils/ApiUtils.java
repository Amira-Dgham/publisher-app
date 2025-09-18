package com.mobelite.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.mobelite.config.ConfigManager;
import com.mobelite.models.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Function;

@Slf4j
public class ApiUtils {
    private final APIRequestContext api;
    private final ConfigManager config;

    public ApiUtils(APIRequestContext api) {
        this.api = api;
        this.config = ConfigManager.getInstance();
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

    // Generic delete by ID
    public boolean deleteById(String endpoint, Long id) {
        APIResponse response = api.delete(endpoint + "/" + id);
        return response.ok();
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