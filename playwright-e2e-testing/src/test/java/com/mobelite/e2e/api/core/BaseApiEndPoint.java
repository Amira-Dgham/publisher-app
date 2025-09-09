package com.mobelite.e2e.api.core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mobelite.e2e.api.models.ApiResponse;
import com.mobelite.e2e.api.models.PageResponse;
import com.mobelite.e2e.api.utils.PlaywrightSchemaValidator;
import com.mobelite.e2e.config.BaseTest;
import com.mobelite.e2e.shared.constants.HttpMethod;
import com.microsoft.playwright.APIRequestContext;
import com.mobelite.e2e.shared.constants.HttpStatusCodes;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public abstract class BaseApiEndPoint<T, R> extends BaseTest {

    protected ApiClient apiClient;
    protected T sharedEntity;
    private final List<Long> entitiesToCleanup = new ArrayList<>();

    // ---- Required per-entity ----
    protected abstract String getEntityName();
    protected abstract String getItemSchema();
    protected abstract TypeReference<ApiResponse<T>> getItemTypeReference();
    protected abstract TypeReference<ApiResponse<PageResponse<T>>> getPageTypeReference();

    // ---- Defaults ----
    protected String getPageResponseSchema() { return "/schemas/page-response-schema.json"; }
    protected String getApiResponseSchema() { return "/schemas/api-response-schema.json"; }

   // todo
    public void init(APIRequestContext api) {
        this.apiClient = new ApiClient(api);
    }

    public void cleanUpEach(String cleanupUrl) {

        for (Long id : entitiesToCleanup) {
            log.info("cleantup dattaa",id);
            try {
                deleteAndValidate(id, cleanupUrl);
                log.info("Cleaned up {} {}", getEntityName(), id);
            } catch (Exception e) {
                log.warn("Failed to delete {} {}: {}", getEntityName(), id, e.getMessage());
            }
        }
        entitiesToCleanup.clear();
    }

    // ---- Generic CRUD operations ----
    public ApiResponse<T> create(R request, String endpoint) {
        return executeRequest(new ApiRequestBuilder(apiClient, HttpMethod.POST, endpoint).body(request), HttpStatusCodes.STATUS_CREATED, getItemTypeReference());
    }

    public T createAndValidate(R request, String endpoint) {
        return validateResponseStructure(
                create(request, endpoint),
                getApiResponseSchema(),
                getItemSchema(),
                null,
                "created successfully"
        );
    }

    public ApiResponse<T> getById(Long id, String endpoint) {
        return executeRequest(new ApiRequestBuilder(apiClient, HttpMethod.GET,buildPath(endpoint, id)), 200, getItemTypeReference());
    }

    public T getByIdAndValidate(Long id, String endpoint) {
        return validateResponseStructure(getById(id, endpoint), getApiResponseSchema(), getItemSchema(), null, "retrieved successfully");
    }

    public PageResponse<T> getAllAndValidate(String endpoint) {
        return validateResponseStructure(executeRequest(new ApiRequestBuilder(apiClient, HttpMethod.GET,endpoint), 200, getPageTypeReference()), getApiResponseSchema(), getPageResponseSchema(), getItemSchema(), "Operation successful");
    }

    public ApiResponse<Void> delete(Long id, String endpoint) {
        return executeRequest(new ApiRequestBuilder(apiClient, HttpMethod.DELETE,buildPath(endpoint, id)), 200, new TypeReference<ApiResponse<Void>>() {});
    }

    public ApiResponse<Void> deleteAndValidate(Long id, String endpoint) {
        ApiResponse<Void> response = delete(id, endpoint);
        ApiAssertions.assertMessageContains(response, "deleted successfully");
        return response;
    }

    public void trackForCleanup(Long id) { entitiesToCleanup.add(id); }

    protected <U> ApiResponse<U> executeRequest(ApiRequestBuilder builder, int expectedStatus, TypeReference<ApiResponse<U>> typeRef) {
        var response = builder.execute();
        ApiAssertions.assertSuccess(response, expectedStatus);
        return apiClient.parseResponse(response, typeRef);
    }

    public ApiResponse<?> executeErrorRequest(ApiRequestBuilder builder, int expectedStatus) {
        var response = builder.execute();
        ApiAssertions.assertSuccess(response, expectedStatus);
        return apiClient.parseErrorResponse(response);
    }

    protected <U> U validateResponseStructure(ApiResponse<U> response, String apiSchema, String dataSchema, String contentSchema, String expectedMessage) {
        PlaywrightSchemaValidator.validateResponseAndData(response, apiSchema, dataSchema, contentSchema);
        ApiAssertions.assertSuccess(response);
        ApiAssertions.assertHasData(response);
        if (expectedMessage != null) ApiAssertions.assertMessageContains(response, expectedMessage);
        return response.getData();
    }


    protected String buildPath(String template, Object... params) {
        String path = template;
        for (Object param : params) path = path.replaceFirst("\\{[^}]+\\}", String.valueOf(param));
        return path;
    }
}