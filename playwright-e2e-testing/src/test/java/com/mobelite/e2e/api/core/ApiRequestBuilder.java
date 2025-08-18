package com.mobelite.e2e.apis.core;

import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * ApiRequestBuilder is a fluent builder class used to construct and execute HTTP API requests.
 *
 * It supports common HTTP methods (GET, POST, PUT, PATCH, DELETE) and allows setting
 * request headers, query parameters, request body, and authentication tokens.
 */
@Slf4j
public class ApiRequestBuilder {

    /**
     * Supported HTTP methods for API requests.
     */
    public enum HttpMethod {
        GET, POST, PUT, PATCH, DELETE
    }

    private final ApiClient apiClient;
    private String endpoint;
    private HttpMethod method = HttpMethod.GET;
    private Object body;
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> queryParams = new HashMap<>();

    public ApiRequestBuilder(ApiClient apiClient, HttpMethod method, String endpoint) {
        this.apiClient = apiClient;
        this.method = method;
        this.endpoint = endpoint;
    }

    /**
     * Sets the request body payload to be sent.
     *
     * @param body the body object, which will be serialized to JSON
     * @return the current ApiRequestBuilder instance for chaining
     */
    public ApiRequestBuilder body(Object body) {
        this.body = body;
        return this;
    }

    /**
     * Adds a single HTTP header to the request.
     *
     * @param key the header name
     * @param value the header value
     * @return the current ApiRequestBuilder instance for chaining
     */
    public ApiRequestBuilder header(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    /**
     * Adds multiple HTTP headers to the request.
     *
     * @param headers a map of header key-value pairs
     * @return the current ApiRequestBuilder instance for chaining
     */
    public ApiRequestBuilder headers(Map<String, String> headers) {
        if (headers != null) {
            this.headers.putAll(headers);
        }
        return this;
    }

    /**
     * Adds a query parameter to the request URL.
     *
     * @param key the query parameter name
     * @param value the query parameter value
     * @return the current ApiRequestBuilder instance for chaining
     */
    public ApiRequestBuilder queryParam(String key, String value) {
        this.queryParams.put(key, value);
        return this;
    }

    /**
     * Adds an Authorization header with a Bearer token.
     *
     * @param token the bearer token string
     * @return the current ApiRequestBuilder instance for chaining
     */
    public ApiRequestBuilder auth(String token) {
        return header("Authorization", "Bearer " + token);
    }

    /**
     * Adds an API key header with a custom header name.
     *
     * @param keyName the header name for the API key (e.g., "X-API-KEY")
     * @param apiKey the API key value
     * @return the current ApiRequestBuilder instance for chaining
     */
    public ApiRequestBuilder apiKey(String keyName, String apiKey) {
        return header(keyName, apiKey);
    }

    /**
     * Executes the built HTTP request using the specified HTTP method,
     * endpoint, headers, query parameters, and body.
     *
     * The request is sent via the underlying ApiClient, and the APIResponse
     * from the server is returned.
     *
     * This method is instrumented with Allure's @Step annotation for reporting.
     *
     * @return the APIResponse received from the server
     */
    @Step("Execute {method} {endpoint}")
    public APIResponse execute() {
        String finalEndpoint = buildEndpointWithParams();
        RequestOptions options = buildRequestOptions();

        return switch (method) {
            case GET -> apiClient.get(finalEndpoint, options);
            case POST -> apiClient.post(finalEndpoint, options);
            case PUT -> apiClient.put(finalEndpoint, options);
            case PATCH -> apiClient.patch(finalEndpoint, options);
            case DELETE -> apiClient.delete(finalEndpoint, options);
        };
    }

    /**
     * Builds the RequestOptions object including headers and JSON body data (if present)
     * to be sent with the HTTP request.
     *
     * @return the constructed RequestOptions object
     */
    private RequestOptions buildRequestOptions() {
        RequestOptions options = RequestOptions.create();

        // Add headers
        headers.forEach(options::setHeader);

        // Add body data if present (serialized as JSON)
        if (body != null) {
            options.setData(apiClient.toJson(body));
        }

        return options;
    }

    /**
     * Constructs the final endpoint URL by appending query parameters if any exist.
     *
     * Example:
     * - endpoint: "/users"
     * - queryParams: { "page" = "2", "sort" = "asc" }
     * Result: "/users?page=2&sort=asc"
     *
     * @return the endpoint URL string with appended query parameters
     */
    private String buildEndpointWithParams() {
        if (queryParams.isEmpty()) {
            return endpoint;
        }

        StringJoiner joiner = new StringJoiner("&", endpoint + "?", "");
        queryParams.forEach((key, value) -> joiner.add(key + "=" + value));
        return joiner.toString();
    }
}