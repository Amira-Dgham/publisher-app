package com.mobelite.e2e.api.endpoints;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIResponse;
import com.mobelite.e2e.api.core.ApiAssertions;
import com.mobelite.e2e.api.core.ApiClient;
import com.mobelite.e2e.api.core.ApiRequestBuilder;
import com.mobelite.e2e.api.models.ApiResponse;
import com.mobelite.e2e.api.utils.PlaywrightSchemaValidator;
import com.mobelite.e2e.shared.constants.HttpMethod;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseEndpoints {

    protected final ApiClient apiClient;

    protected BaseEndpoints(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    // -------- Factory methods for creating ApiRequestBuilder instances -------- //

    protected ApiRequestBuilder get(String endpoint) {
        return new ApiRequestBuilder(apiClient, HttpMethod.GET, endpoint);
    }

    protected ApiRequestBuilder post(String endpoint) {
        return new ApiRequestBuilder(apiClient, HttpMethod.POST, endpoint);
    }

    protected ApiRequestBuilder put(String endpoint) {
        return new ApiRequestBuilder(apiClient, HttpMethod.PUT, endpoint);
    }

    protected ApiRequestBuilder delete(String endpoint) {
        return new ApiRequestBuilder(apiClient, HttpMethod.DELETE, endpoint);
    }

    protected ApiRequestBuilder patch(String endpoint) {
        return new ApiRequestBuilder(apiClient, HttpMethod.PATCH, endpoint);
    }

    protected String buildPath(String template, Object... params) {
        String path = template;
        for (Object param : params) {
            path = path.replaceFirst("\\{[^}]+\\}", String.valueOf(param));
        }
        return path;
    }

    // ---------------------- GENERIC HELPER METHODS ---------------------- //

    protected <T> ApiResponse<T> executeRequest(ApiRequestBuilder builder, int expectedStatus, TypeReference<ApiResponse<T>> typeRef) {
        APIResponse httpResponse = builder.execute();
        ApiAssertions.assertSuccess(httpResponse, expectedStatus);
        return apiClient.parseResponse(httpResponse, typeRef);
    }

    protected ApiResponse<?> executeErrorRequest(ApiRequestBuilder builder, int expectedStatus) {
        APIResponse httpResponse = builder.execute();
        ApiAssertions.assertSuccess(httpResponse, expectedStatus);
        return apiClient.parseErrorResponse(httpResponse);
    }

    protected <T> T validateResponseStructure(ApiResponse<T> response,
                                              String schemaPath,
                                              String dataSchemaPath,
                                              String contentSchemaPath,
                                              String expectedMessage) {

        // Delegate all JSON parsing + schema validation to PlaywrightSchemaValidator
        PlaywrightSchemaValidator.validateResponseAndData(
                response,
                schemaPath,
                dataSchemaPath,
                contentSchemaPath
        );

        // Domain-level assertions
        ApiAssertions.assertSuccess(response);
        ApiAssertions.assertHasData(response);

        if (expectedMessage != null) {
            ApiAssertions.assertMessageContains(response, expectedMessage);
        }

        return response.getData();
    }
}