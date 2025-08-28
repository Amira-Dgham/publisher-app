package com.mobelite.e2e.api.core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mobelite.e2e.api.models.ApiResponse;
import com.mobelite.e2e.api.models.PageResponse;
import com.mobelite.e2e.api.utils.PlaywrightSchemaValidator;
import com.mobelite.e2e.config.BaseTest;
import com.mobelite.e2e.shared.constants.HttpMethod;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public abstract class BaseApiEndPoint<T, R> extends BaseTest {

    protected ApiClient apiClient;
    protected T sharedEntity;
    private final List<Long> entitiesToCleanup = new ArrayList<>();

    // ---- Required per-entity ----
    protected abstract String getEntityName();
    protected abstract String getItemSchema();
    protected abstract R createSharedEntityRequest();
    protected abstract TypeReference<ApiResponse<T>> getItemTypeReference();
    protected abstract TypeReference<ApiResponse<PageResponse<T>>> getPageTypeReference();
    protected abstract String getBaseEndpoint();
    protected abstract String getItemByIdEndpoint();

    // ---- Defaults ----
    protected String getPageResponseSchema() { return "/schemas/page-response-schema.json"; }
    protected String getApiResponseSchema() { return "/schemas/api-response-schema.json"; }

    // ---- Optional shared entity creation ----
    protected boolean shouldCreateSharedEntity() { return true; }

    @BeforeAll
    void init() {
        this.apiClient = new ApiClient(api);

        if (shouldCreateSharedEntity()) {
            R request = createSharedEntityRequest();
            if (request != null) {
                this.sharedEntity = createAndValidate(request, getBaseEndpoint());
                log.info("Shared {} created with ID: {}", getEntityName(), getId(sharedEntity));
            } else {
                log.warn("Shared entity creation skipped because createSharedEntityRequest() returned null");
            }
        }
    }

    @AfterAll
    void tearDown() {
        if (sharedEntity != null) {
            try {
                deleteAndValidate(getId(sharedEntity), getItemByIdEndpoint());
                log.info("Shared {} deleted: {}", getEntityName(), getId(sharedEntity));
            } catch (Exception e) {
                log.warn("Failed to delete shared {}: {}", getEntityName(), e.getMessage());
            }
        }
    }

    @AfterEach
    void cleanUpEach() {
        for (Long id : entitiesToCleanup) {
            try {
                deleteAndValidate(id, getItemByIdEndpoint());
                log.info("Cleaned up {} {}", getEntityName(), id);
            } catch (Exception e) {
                log.warn("Failed to delete {} {}: {}", getEntityName(), id, e.getMessage());
            }
        }
        entitiesToCleanup.clear();
    }

    // ---- Generic CRUD operations ----
    public ApiResponse<T> create(R request, String endpoint) {
        return executeRequest(post(endpoint).body(request), 201, getItemTypeReference());
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
        return executeRequest(get(buildPath(endpoint, id)), 200, getItemTypeReference());
    }

    public T getByIdAndValidate(Long id, String endpoint) {
        return validateResponseStructure(getById(id, endpoint), getApiResponseSchema(), getItemSchema(), null, "retrieved successfully");
    }

    public PageResponse<T> getAllAndValidate(String endpoint) {
        return validateResponseStructure(executeRequest(get(endpoint), 200, getPageTypeReference()), getApiResponseSchema(), getPageResponseSchema(), getItemSchema(), "Operation successful");
    }

    public ApiResponse<Void> delete(Long id, String endpoint) {
        return executeRequest(delete(buildPath(endpoint, id)), 200, new TypeReference<ApiResponse<Void>>() {});
    }

    public ApiResponse<Void> deleteAndValidate(Long id, String endpoint) {
        ApiResponse<Void> response = delete(id, endpoint);
        ApiAssertions.assertMessageContains(response, "deleted successfully");
        return response;
    }

    protected void trackForCleanup(Long id) { entitiesToCleanup.add(id); }

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

    @SuppressWarnings("unchecked")
    protected Long getId(T entity) {
        try {
            var idMethod = entity.getClass().getMethod("getId");
            return (Long) idMethod.invoke(entity);
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract ID from entity: " + entity, e);
        }
    }

    protected <U> U validateResponseStructure(ApiResponse<U> response, String apiSchema, String dataSchema, String contentSchema, String expectedMessage) {
        PlaywrightSchemaValidator.validateResponseAndData(response, apiSchema, dataSchema, contentSchema);
        ApiAssertions.assertSuccess(response);
        ApiAssertions.assertHasData(response);
        if (expectedMessage != null) ApiAssertions.assertMessageContains(response, expectedMessage);
        return response.getData();
    }

    // --- HTTP builders ---
    protected ApiRequestBuilder post(String endpoint) { return new ApiRequestBuilder(apiClient, HttpMethod.POST, endpoint); }
    protected ApiRequestBuilder get(String endpoint) { return new ApiRequestBuilder(apiClient, HttpMethod.GET, endpoint); }
    protected ApiRequestBuilder put(String endpoint) { return new ApiRequestBuilder(apiClient, HttpMethod.PUT, endpoint); }
    protected ApiRequestBuilder delete(String endpoint) { return new ApiRequestBuilder(apiClient, HttpMethod.DELETE, endpoint); }

    protected String buildPath(String template, Object... params) {
        String path = template;
        for (Object param : params) path = path.replaceFirst("\\{[^}]+\\}", String.valueOf(param));
        return path;
    }
}