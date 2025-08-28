package com.mobelite.e2e.api.core;

import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import com.mobelite.e2e.shared.constants.HttpMethod;
import io.qameta.allure.Step;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class ApiRequestBuilder {

    private final ApiClient apiClient;
    private final HttpMethod method;
    private final String endpoint;

    private Object body;
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> queryParams = new HashMap<>();

    // --- Fluent builders ---
    public ApiRequestBuilder body(Object body) {
        this.body = body;
        return this;
    }

    public ApiRequestBuilder header(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public ApiRequestBuilder headers(Map<String, String> headers) {
        if (headers != null) headers.forEach(this::header);
        return this;
    }

    public ApiRequestBuilder queryParam(String key, String value) {
        queryParams.put(key, value);
        return this;
    }

    public ApiRequestBuilder auth(String token) {
        return header("Authorization", "Bearer " + token);
    }

    public ApiRequestBuilder apiKey(String keyName, String apiKey) {
        return header(keyName, apiKey);
    }

    // --- Execution ---
    @Step("Execute {method} {endpoint}")
    public APIResponse execute() {
        return apiClient.execute(method, buildEndpointWithParams(), buildRequestOptions());
    }

    // --- Internals ---
    private RequestOptions buildRequestOptions() {
        RequestOptions options = RequestOptions.create();
        headers.forEach(options::setHeader);
        if (body != null) {
            options.setData(apiClient.toJson(body));
        }
        return options;
    }

    private String buildEndpointWithParams() {
        if (queryParams.isEmpty()) return endpoint;
        return endpoint + "?" + queryParams.entrySet().stream()
                .map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8)
                        + "=" +
                        URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
    }
}