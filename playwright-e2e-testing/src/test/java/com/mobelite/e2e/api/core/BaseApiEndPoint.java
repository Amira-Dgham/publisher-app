package com.mobelite.e2e.api.core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mobelite.e2e.api.models.ApiResponse;
import com.mobelite.e2e.api.models.PageResponse;
import com.mobelite.e2e.config.BaseTest;
import com.mobelite.e2e.shared.constants.HttpMethod;
import com.microsoft.playwright.APIRequestContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.mobelite.e2e.shared.helpers.ApiUtils.buildPath;

@Slf4j
public abstract class BaseApiEndPoint<T, R> extends BaseTest {

    @Getter
    protected ApiClient apiClient;
    private final List<Long> entitiesToCleanup = new ArrayList<>();

    // ---- Required per-entity ----
    protected abstract String getEntityName();
    protected abstract String getItemSchema();
    protected abstract TypeReference<ApiResponse<T>> getItemTypeReference();
    protected abstract TypeReference<ApiResponse<PageResponse<T>>> getPageTypeReference();

    // ---- Defaults ----
    protected String getPageResponseSchema() { return "/schemas/page-response-schema.json"; }
    protected String getApiResponseSchema() { return "/schemas/api-response-schema.json"; }

    // ---- Initialization ----
    public void init(APIRequestContext api) {
        this.apiClient = new ApiClient(api);
    }

    public void cleanUpEach(String cleanupUrl) {
        log.info("Cleaning up {} for IDs: {}", getEntityName(), entitiesToCleanup);

        for (Long id : entitiesToCleanup) {
            try {
                // Attempt deletion only
                deleteAndValidate(id, cleanupUrl);
                log.info("Cleaned up {} {}", getEntityName(), id);
            } catch (Exception e) {
                log.warn("Failed to delete {} {}: {}", getEntityName(), id, e.getMessage());
            }
        }
        entitiesToCleanup.clear();
    }


    // ---- Generic CRUD operations using ApiClient#executeAndValidate ----
    public T createAndValidate(R request, String endpoint) {
        return apiClient.executeAndValidate(
                new ApiRequestBuilder(apiClient, HttpMethod.POST, endpoint).body(request),
                getItemTypeReference(),
                getApiResponseSchema(),
                getItemSchema(),
                null
        ).getData();
    }

    public T getByIdAndValidate(Long id, String endpoint) {
        return apiClient.executeAndValidate(
                new ApiRequestBuilder(apiClient, HttpMethod.GET, buildPath(endpoint, id)),
                getItemTypeReference(),
                getApiResponseSchema(),
                getItemSchema(),
                null
        ).getData();
    }

    public PageResponse<T> getAllAndValidate(String endpoint) {
        return apiClient.executeAndValidate(
                new ApiRequestBuilder(apiClient, HttpMethod.GET, endpoint),
                getPageTypeReference(),
                getApiResponseSchema(),
                getPageResponseSchema(),
                getItemSchema()
        ).getData();
    }

    public ApiResponse<Void> deleteAndValidate(Long id, String endpoint) {
        ApiResponse<Void> response = apiClient.executeAndValidate(
                new ApiRequestBuilder(apiClient, HttpMethod.DELETE, buildPath(endpoint, id)),
                new TypeReference<ApiResponse<Void>>() {},
                getApiResponseSchema(),
                null,
                null
        );
        ApiAssertions.assertMessageContains(response, "deleted successfully");
        return response;
    }

    // ---- Negative GET (expect failure) ----
    public ApiResponse<?> executeInvalidGet(Long id, String endpoint, int expectedStatus) {
        var response = new ApiRequestBuilder(apiClient, HttpMethod.GET, buildPath(endpoint, id)).execute();
        ApiAssertions.assertStatus(response, expectedStatus);
        return apiClient.parseErrorResponse(response);
    }

    public ApiResponse<?> executeInvalidPost(R request, String endpoint, int expectedStatus) {
        var response = new ApiRequestBuilder(apiClient, HttpMethod.POST, endpoint).body(request).execute();
        ApiAssertions.assertStatus(response, expectedStatus);
        return apiClient.parseErrorResponse(response);
    }

    public ApiResponse<?> executeInvalidDelete(Long id, String endpoint, int expectedStatus) {
        var response = new ApiRequestBuilder(apiClient, HttpMethod.DELETE, buildPath(endpoint, id)).execute();
        ApiAssertions.assertStatus(response, expectedStatus);
        return apiClient.parseErrorResponse(response);
    }

    public void trackForCleanup(Long id) { entitiesToCleanup.add(id); }
}