package com.mobelite.publisher.api.base;

import com.fasterxml.jackson.core.type.TypeReference;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import com.mobelite.publisher.api.constants.HttpMethod;
import com.mobelite.publisher.api.models.response.ApiResponse;
import com.mobelite.publisher.api.models.response.PageResponse;
import com.mobelite.publisher.api.utils.ApiUtils;
import com.mobelite.publisher.api.utils.PlaywrightSchemaValidatorUtils;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class BaseTestApi<T, R> extends BaseTest {

    protected ApiClient apiClient;
    private final List<Long> createdEntities = new ArrayList<>();
    protected abstract TypeReference<ApiResponse<T>> getItemTypeReference();
    protected abstract TypeReference<ApiResponse<PageResponse<T>>> getPageTypeReference();

    // ---- Initialization ----
    public void init(APIRequestContext api) {
        this.apiClient = new ApiClient(api, this);
    }

    protected void trackCreatedEntity(Long id) {
        createdEntities.add(id);
    }

    public void cleanupEntities(String endpoint) {
        for (Long id : createdEntities) {
            try {
                delete(id, endpoint);
            } catch (Exception e) {
                log.warn("Failed to cleanup entity with id {}: {}", id, e.getMessage());
            }
        }
        createdEntities.clear();
    }

    // ---- CRUD helpers ----
    public APIResponse create(R request, String endpoint) {
        RequestOptions options = ApiUtils.buildRequestOptions(request, null, apiClient);
        return apiClient.execute(HttpMethod.POST, endpoint, options);
    }

    public APIResponse getById(Long id, String endpoint) {
        String url = ApiUtils.buildPath(endpoint, id);
        return apiClient.execute(HttpMethod.GET, url, null);
    }

    public APIResponse getAll(String endpoint) {
        return getAll(endpoint, null, null);
    }
    public APIResponse getAll(String endpoint, Map<String, String> queryParams) {
        return getAll(endpoint, queryParams, null);
    }

    public APIResponse getAll(String endpoint, Map<String, String> queryParams, Map<String, String> headers) {
        String url = ApiUtils.buildEndpointWithParams(endpoint, queryParams);
        RequestOptions options = ApiUtils.buildRequestOptions(null, headers, apiClient);
        return apiClient.execute(HttpMethod.GET, url, options);
    }

    public APIResponse delete(Long id, String endpoint) {
        String url = ApiUtils.buildPath(endpoint, id);
        return apiClient.execute(HttpMethod.DELETE, url, null);
    }

    // ---- Parse & Validate ----
    protected ApiResponse<T> parseAndValidate(APIResponse response, String schema, String dataSchema) {
        ApiResponse<T> parsed = apiClient.parseResponse(response, getItemTypeReference());
        PlaywrightSchemaValidatorUtils.validateResponseAndData(parsed, schema, dataSchema, null);
        return parsed;
    }
}