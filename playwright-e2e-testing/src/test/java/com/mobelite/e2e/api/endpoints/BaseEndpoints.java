package com.mobelite.e2e.apis.endpoints;

import com.microsoft.playwright.APIResponse;
import com.mobelite.e2e.apis.core.ApiClient;
import com.mobelite.e2e.apis.core.ApiRequestBuilder;
import com.mobelite.e2e.apis.models.ApiResponse;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

/**
 * BaseEndpoints is an abstract base class providing common HTTP request
 * methods, response validation, and utility functions for API endpoint classes.
 *
 * It serves as a foundation to create specific endpoint classes that interact
 * with the API through the ApiClient and ApiRequestBuilder abstractions.
 *
 * Responsibilities:
 * - Provide factory methods to create requests with common HTTP verbs (GET, POST, PUT, DELETE, PATCH).
 * - Provide reusable validation methods to verify response status, success, data presence, and messages.
 * - Provide utility methods to parse API responses and build endpoint paths with parameters.
 *
 * This promotes code reuse and consistency in endpoint implementations,
 * and leverages Allure @Step annotations for enhanced test reporting.
 */
@Slf4j
public abstract class BaseEndpoints {

    /**
     * The ApiClient instance used to perform HTTP requests and parse responses.
     */
    protected final ApiClient apiClient;

    /**
     * Constructs the BaseEndpoints with a provided ApiClient.
     *
     * @param apiClient the ApiClient instance to be used for sending requests
     */
    protected BaseEndpoints(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    // -------- Factory methods for creating ApiRequestBuilder instances -------- //

    /**
     * Creates an ApiRequestBuilder configured for an HTTP GET request to the specified endpoint.
     *
     * @param endpoint the API endpoint path (e.g., "/users")
     * @return an ApiRequestBuilder for a GET request
     */
    protected ApiRequestBuilder get(String endpoint) {
        return new ApiRequestBuilder(apiClient, ApiRequestBuilder.HttpMethod.GET, endpoint);
    }

    /**
     * Creates an ApiRequestBuilder configured for an HTTP POST request to the specified endpoint.
     *
     * @param endpoint the API endpoint path
     * @return an ApiRequestBuilder for a POST request
     */
    protected ApiRequestBuilder post(String endpoint) {
        return new ApiRequestBuilder(apiClient, ApiRequestBuilder.HttpMethod.POST, endpoint);
    }

    /**
     * Creates an ApiRequestBuilder configured for an HTTP PUT request to the specified endpoint.
     *
     * @param endpoint the API endpoint path
     * @return an ApiRequestBuilder for a PUT request
     */
    protected ApiRequestBuilder put(String endpoint) {
        return new ApiRequestBuilder(apiClient, ApiRequestBuilder.HttpMethod.PUT, endpoint);
    }

    /**
     * Creates an ApiRequestBuilder configured for an HTTP DELETE request to the specified endpoint.
     *
     * @param endpoint the API endpoint path
     * @return an ApiRequestBuilder for a DELETE request
     */
    protected ApiRequestBuilder delete(String endpoint) {
        return new ApiRequestBuilder(apiClient, ApiRequestBuilder.HttpMethod.DELETE, endpoint);
    }

    /**
     * Creates an ApiRequestBuilder configured for an HTTP PATCH request to the specified endpoint.
     *
     * @param endpoint the API endpoint path
     * @return an ApiRequestBuilder for a PATCH request
     */
    protected ApiRequestBuilder patch(String endpoint) {
        return new ApiRequestBuilder(apiClient, ApiRequestBuilder.HttpMethod.PATCH, endpoint);
    }

    // -------- Validation methods to assert correctness of API responses -------- //

    /**
     * Validates that the API response status code matches the expected status code.
     * Throws AssertionError with detailed message if it does not match.
     *
     * @param response the API response to validate
     * @param expectedStatus the expected HTTP status code (e.g., 200)
     */
    @Step("Validate response status: {expectedStatus}")
    protected void validateStatus(APIResponse response, int expectedStatus) {
        int actualStatus = response.status();
        if (actualStatus != expectedStatus) {
            String errorMessage = String.format(
                    "Expected status %d but got %d. Response: %s",
                    expectedStatus, actualStatus, response.text()
            );
            log.error(errorMessage);
            throw new AssertionError(errorMessage);
        }
    }

    /**
     * Validates that the API response status code indicates success (any 2xx status).
     * Throws AssertionError if the status is outside the 2xx range.
     *
     * @param response the API response to validate
     */
    @Step("Validate successful response")
    protected void validateSuccess(APIResponse response) {
        int status = response.status();
        if (status < 200 || status >= 300) {
            String errorMessage = String.format(
                    "Expected successful response (2xx) but got %d. Response: %s",
                    status, response.text()
            );
            log.error(errorMessage);
            throw new AssertionError(errorMessage);
        }
    }

    /**
     * Validates that the parsed API response contains non-null data.
     * Throws AssertionError if response or data is null.
     *
     * @param response the parsed ApiResponse object
     */
    @Step("Validate response has data")
    protected void validateHasData(ApiResponse<?> response) {
        if (response == null || response.getData() == null) {
            throw new AssertionError("Response or data is null");
        }
    }

    /**
     * Validates that the response message contains the expected substring.
     * Throws AssertionError if the message is null or does not contain the expected text.
     *
     * @param response the parsed ApiResponse object
     * @param expectedMessage the expected substring to be found in the message
     */
    @Step("Validate response message contains: {expectedMessage}")
    protected void validateMessageContains(ApiResponse<?> response, String expectedMessage) {
        String message = response.getMessage();
        if (message == null || !message.contains(expectedMessage)) {
            throw new AssertionError(String.format(
                    "Expected message to contain '%s' but was '%s'",
                    expectedMessage, message
            ));
        }
    }

    // -------- Utility methods for parsing responses and building paths -------- //

    /**
     * Parses the API response body into the specified Java class using ApiClient's parser.
     *
     * @param <T> the type of the parsed object
     * @param response the APIResponse object to parse
     * @param clazz the class of the type to parse to
     * @return the parsed object
     */
    protected <T> T parseResponse(APIResponse response, Class<T> clazz) {
        return apiClient.parseResponse(response, clazz);
    }

    /**
     * Parses an error response to ApiResponse.
     * If parsing fails, creates a fallback ApiResponse with success=false and raw message text.
     *
     * @param response the APIResponse object containing the error
     * @return the parsed ApiResponse error object or fallback with raw text
     */
    protected ApiResponse<?> parseErrorResponse(APIResponse response) {
        try {
            return parseResponse(response, ApiResponse.class);
        } catch (Exception e) {
            log.warn("Failed to parse error response: {}", e.getMessage());
            ApiResponse<Object> errorResponse = new ApiResponse<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage(response.text());
            return errorResponse;
        }
    }

    /**
     * Builds a formatted path by replacing placeholders (e.g., {id}) with provided parameters.
     * Useful for constructing dynamic endpoints.
     *
     * Example:
     * buildPath("/users/{id}/posts/{postId}", 123, 456) -> "/users/123/posts/456"
     *
     * @param template the path template containing placeholders like {param}
     * @param params the parameters to replace placeholders with
     * @return the formatted path string
     */
    protected String buildPath(String template, Object... params) {
        String path = template;
        for (Object param : params) {
            path = path.replaceFirst("\\{[^}]+\\}", String.valueOf(param));
        }
        return path;
    }
}