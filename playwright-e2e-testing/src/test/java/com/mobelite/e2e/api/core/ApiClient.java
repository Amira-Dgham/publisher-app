package com.mobelite.e2e.api.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.RequestOptions;
import com.mobelite.e2e.config.TestConfig;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApiClient implements AutoCloseable {

    private final APIRequestContext requestContext;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    public ApiClient(APIRequestContext apiRequestContext) {
        TestConfig config = TestConfig.getInstance();
        this.baseUrl = config.getApiBaseUrl();
        this.objectMapper = config.getObjectMapper();
        this.requestContext = apiRequestContext;
    }

    @Step("GET {endpoint}")
    public APIResponse get(String endpoint, RequestOptions options) {
        return executeRequest("GET", endpoint, options, () ->
                requestContext.get(endpoint, options != null ? options : RequestOptions.create()));
    }

    @Step("POST {endpoint}")
    public APIResponse post(String endpoint, RequestOptions options) {
        return executeRequest("POST", endpoint, options, () ->
                requestContext.post(endpoint, options != null ? options : RequestOptions.create()));
    }

    @Step("PUT {endpoint}")
    public APIResponse put(String endpoint, RequestOptions options) {
        return executeRequest("PUT", endpoint, options, () ->
                requestContext.put(endpoint, options != null ? options : RequestOptions.create()));
    }

    @Step("PATCH {endpoint}")
    public APIResponse patch(String endpoint, RequestOptions options) {
        return executeRequest("PATCH", endpoint, options, () ->
                requestContext.patch(endpoint, options != null ? options : RequestOptions.create()));
    }

    @Step("DELETE {endpoint}")
    public APIResponse delete(String endpoint, RequestOptions options) {
        return executeRequest("DELETE", endpoint, options, () ->
                requestContext.delete(endpoint, options != null ? options : RequestOptions.create()));
    }

    private APIResponse executeRequest(String method, String endpoint, RequestOptions options,
                                       java.util.function.Supplier<APIResponse> requestSupplier) {
        log.info("Executing {} request to: {}", method, endpoint);
        try {
            APIResponse response = requestSupplier.get();
            log.info("Response: {} {}", response.status(), response.statusText());
            return response;
        } catch (Exception e) {
            log.error("Request failed: {} {}", method, endpoint, e);
            throw new RuntimeException(String.format("API request failed: %s %s", method, endpoint), e);
        }
    }

    public String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize object to JSON", e);
        }
    }

    /**
     * Parses the API response body into the specified Java class.
     *
     * @param <T> the type of the parsed object
     * @param response the APIResponse object to parse
     * @param clazz the target class type
     * @return the parsed object
     */
    public <T> T parseResponse(APIResponse response, Class<T> clazz) {
        try {
            String responseText = response.text();
            log.debug("Parsing response to {}: {}", clazz.getSimpleName(), responseText);
            return objectMapper.readValue(responseText, clazz);
        } catch (Exception e) {
            log.error("Failed to parse API response to {}: {}", clazz.getSimpleName(), e.getMessage());
            throw new RuntimeException("Failed to parse API response", e);
        }
    }

    /**
     * Parses the API response body using TypeReference for complex generic types.
     * This method properly handles generic type information, avoiding ClassCastException.
     *
     * @param <T> the type of the parsed object
     * @param response the APIResponse object to parse
     * @param typeReference the TypeReference containing the target type information
     * @return the parsed object
     */
    public <T> T parseResponse(APIResponse response, TypeReference<T> typeReference) {
        try {
            String responseText = response.text();
            log.debug("Parsing response using TypeReference: {}", responseText);
            return objectMapper.readValue(responseText, typeReference);
        } catch (Exception e) {
            log.error("Failed to parse API response using TypeReference: {}", e.getMessage());
            throw new RuntimeException("Failed to parse API response", e);
        }
    }

    public RequestOptions createJsonOptions(Object body) {
        RequestOptions options = RequestOptions.create();
        if (body != null) {
            options.setData(toJson(body));
        }
        return options;
    }

    @Override
    public void close() {
        // Do nothing if the context is managed externally by the extension
        log.debug("ApiClient.close() called, but APIRequestContext lifecycle managed externally.");
    }
}