package com.mobelite.publisher.api.utils;

import com.microsoft.playwright.options.RequestOptions;
import com.mobelite.publisher.api.base.ApiClient;
import lombok.extern.slf4j.Slf4j;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ApiUtils {

    // Existing methods
    public static String buildPath(String template, Object... params) {
        String path = template;
        for (Object param : params) {
            path = path.replaceFirst("\\{[^}]+\\}", String.valueOf(param));
        }
        return path;
    }

    public static String getResponseText(com.microsoft.playwright.APIResponse response) {
        try {
            return response.text();
        } catch (Exception e) {
            log.warn("Failed to read response text: {}", e.getMessage(), e);
            return "";
        }
    }

    // ---- New Utility Methods ----

    /**
     * Builds a Playwright RequestOptions object with optional body and headers
     */
    public static RequestOptions buildRequestOptions(Object body, Map<String, String> headers, ApiClient apiClient) {
        RequestOptions options = RequestOptions.create();
        if (headers != null) {
            headers.forEach(options::setHeader);
        }
        if (body != null) {
            options.setData(apiClient.toJson(body));
        }
        return options;
    }

    /**
     * Builds an endpoint URL with optional query parameters
     */
    public static String buildEndpointWithParams(String endpoint, Map<String, String> queryParams) {
        if (queryParams == null || queryParams.isEmpty()) return endpoint;
        return endpoint + "?" + queryParams.entrySet().stream()
                .map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8) + "="
                        + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
    }
}