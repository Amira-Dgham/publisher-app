package com.mobelite.e2e.api.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.RequestOptions;
import com.mobelite.e2e.config.TestConfig;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * ApiClient is a low-level HTTP client abstraction for interacting with REST APIs
 * using the Playwright APIRequestContext.
 *
 * It provides methods for sending HTTP requests with common methods (GET, POST, PUT, PATCH, DELETE),
 * handles JSON serialization and deserialization with Jackson ObjectMapper,
 * and manages a reusable API request context with configured base URL and headers.
 *
 * This client also integrates with Allure for step reporting and uses Lombok for logging.
 *
 * Usage:
 * <pre>
 * try (ApiClient client = new ApiClient(playwright)) {
 *     APIResponse response = client.get("/users", null);
 *     User user = client.parseResponse(response, User.class);
 * }
 * </pre>
 *
 * Implements AutoCloseable to ensure proper disposal of resources.
 */
@Slf4j
public class ApiClient implements AutoCloseable {

    /**
     * The Playwright APIRequestContext instance used to send HTTP requests.
     */
    private final APIRequestContext requestContext;

    /**
     * Jackson ObjectMapper instance used for JSON serialization/deserialization.
     */
    private final ObjectMapper objectMapper;

    /**
     * The base URL of the API, loaded from configuration.
     */
    private final String baseUrl;

    /**
     * Constructs an ApiClient with the provided Playwright instance.
     *
     * Initializes the API request context with base URL, default headers, and timeout settings.
     * Obtains the ObjectMapper and base URL from the shared TestConfig.
     *
     * @param playwright the Playwright instance used to create the request context
     */
    public ApiClient(Playwright playwright) {
        TestConfig config = TestConfig.getInstance();
        this.baseUrl = config.getApiBaseUrl();
        this.objectMapper = config.getObjectMapper();
        this.requestContext = createRequestContext(playwright);
    }

    /**
     * Creates and configures the Playwright APIRequestContext with base URL,
     * timeout, and default headers such as Content-Type, Accept, and User-Agent.
     *
     * @param playwright the Playwright instance to create the context from
     * @return the configured APIRequestContext
     */
    private APIRequestContext createRequestContext(Playwright playwright) {
        return playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL(baseUrl)
                        .setTimeout(TestConfig.getInstance().getTimeout())
                        .setExtraHTTPHeaders(Map.of(
                                "Content-Type", "application/json",
                                "Accept", "application/json",
                                "User-Agent", "E2E-Test-Client/1.0"
                        ))
        );
    }

    /**
     * Sends a GET request to the specified endpoint with optional request options.
     *
     * @param endpoint the API endpoint path (e.g., "/users")
     * @param options the RequestOptions including headers, body, etc. (can be null)
     * @return the APIResponse from the server
     */
    @Step("GET {endpoint}")
    public APIResponse get(String endpoint, RequestOptions options) {
        return executeRequest("GET", endpoint, options, () ->
                requestContext.get(endpoint, options != null ? options : RequestOptions.create()));
    }

    /**
     * Sends a POST request to the specified endpoint with optional request options.
     *
     * @param endpoint the API endpoint path
     * @param options the RequestOptions including headers, body, etc. (can be null)
     * @return the APIResponse from the server
     */
    @Step("POST {endpoint}")
    public APIResponse post(String endpoint, RequestOptions options) {
        return executeRequest("POST", endpoint, options, () ->
                requestContext.post(endpoint, options != null ? options : RequestOptions.create()));
    }

    /**
     * Sends a PUT request to the specified endpoint with optional request options.
     *
     * @param endpoint the API endpoint path
     * @param options the RequestOptions including headers, body, etc. (can be null)
     * @return the APIResponse from the server
     */
    @Step("PUT {endpoint}")
    public APIResponse put(String endpoint, RequestOptions options) {
        return executeRequest("PUT", endpoint, options, () ->
                requestContext.put(endpoint, options != null ? options : RequestOptions.create()));
    }

    /**
     * Sends a PATCH request to the specified endpoint with optional request options.
     *
     * @param endpoint the API endpoint path
     * @param options the RequestOptions including headers, body, etc. (can be null)
     * @return the APIResponse from the server
     */
    @Step("PATCH {endpoint}")
    public APIResponse patch(String endpoint, RequestOptions options) {
        return executeRequest("PATCH", endpoint, options, () ->
                requestContext.patch(endpoint, options != null ? options : RequestOptions.create()));
    }

    /**
     * Sends a DELETE request to the specified endpoint with optional request options.
     *
     * @param endpoint the API endpoint path
     * @param options the RequestOptions including headers, body, etc. (can be null)
     * @return the APIResponse from the server
     */
    @Step("DELETE {endpoint}")
    public APIResponse delete(String endpoint, RequestOptions options) {
        return executeRequest("DELETE", endpoint, options, () ->
                requestContext.delete(endpoint, options != null ? options : RequestOptions.create()));
    }

    /**
     * Centralized method to execute an HTTP request via the request context.
     * Logs request and response details, and handles exceptions by wrapping them in RuntimeException.
     *
     * @param method the HTTP method (GET, POST, etc.)
     * @param endpoint the API endpoint path
     * @param options the request options, can be null
     * @param requestSupplier the supplier functional interface that executes the actual request
     * @return the APIResponse returned by the request
     */
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

    /**
     * Serializes a Java object to its JSON string representation using Jackson ObjectMapper.
     * Throws RuntimeException if serialization fails.
     *
     * @param obj the object to serialize
     * @return the JSON string representation of the object
     */
    public String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize object to JSON", e);
        }
    }

    /**
     * Parses the APIResponse body text into an instance of the specified class.
     * Logs the response body at debug level and throws RuntimeException if parsing fails.
     *
     * @param <T> the type to parse the response into
     * @param response the APIResponse object
     * @param clazz the class of the type T
     * @return the parsed object instance
     */
    public <T> T parseResponse(APIResponse response, Class<T> clazz) {
        try {
            String responseText = response.text();
            log.debug("Parsing response: {}", responseText);
            return objectMapper.readValue(responseText, clazz);
        } catch (Exception e) {
            log.error("Failed to parse response to {}: {}", clazz.getSimpleName(), e.getMessage());
            throw new RuntimeException("Failed to parse API response", e);
        }
    }

    /**
     * Creates RequestOptions with JSON data if the body is not null.
     * Useful for simplifying request creation with a JSON body.
     *
     * @param body the request body object to serialize as JSON
     * @return RequestOptions object containing serialized JSON data, or empty if body is null
     */
    public RequestOptions createJsonOptions(Object body) {
        RequestOptions options = RequestOptions.create();
        if (body != null) {
            options.setData(toJson(body));
        }
        return options;
    }

    /**
     * Disposes of the APIRequestContext to release any held resources.
     * Called automatically when used in a try-with-resources block.
     */
    @Override
    public void close() {
        if (requestContext != null) {
            requestContext.dispose();
        }
    }
}