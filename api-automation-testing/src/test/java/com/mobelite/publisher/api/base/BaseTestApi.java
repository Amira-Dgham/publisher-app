package com.mobelite.publisher.api.base;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import com.mobelite.publisher.api.constants.HttpMethod;
import com.mobelite.publisher.api.utils.ApiUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class BaseTestApi<R> extends BaseTest {

    protected ApiClient apiClient;
    private final List<Long> entitiesToCleanup = new ArrayList<>();

    // ---- Initialization ----
    public void init(APIRequestContext api) {
        this.apiClient = new ApiClient(api, this);
    }

    // ---- CRUD helpers (raw APIResponse) ----

    public APIResponse create(R request, String endpoint) {
        return create(request, endpoint, null, null);
    }

    public APIResponse create(R request, String endpoint, Map<String, String> headers, Map<String, String> queryParams) {
        String url = ApiUtils.buildEndpointWithParams(endpoint, queryParams);
        RequestOptions options = ApiUtils.buildRequestOptions(request, headers, apiClient);
        return apiClient.execute(HttpMethod.POST, url, options);
    }

    public APIResponse getById(Long id, String endpoint) {
        return getById(id, endpoint, null, null);
    }

    public APIResponse getById(Long id, String endpoint, Map<String, String> headers, Map<String, String> queryParams) {
        String url = ApiUtils.buildEndpointWithParams(endpoint + "/" + id, queryParams);
        RequestOptions options = ApiUtils.buildRequestOptions(null, headers, apiClient);
        return apiClient.execute(HttpMethod.GET, url, options);
    }

    public APIResponse getAll(String endpoint) {
        return getAll(endpoint, null, null);
    }

    public APIResponse getAll(String endpoint, Map<String, String> headers, Map<String, String> queryParams) {
        String url = ApiUtils.buildEndpointWithParams(endpoint, queryParams);
        RequestOptions options = ApiUtils.buildRequestOptions(null, headers, apiClient);
        return apiClient.execute(HttpMethod.GET, url, options);
    }

    public APIResponse delete(Long id, String endpoint) {
        return delete(id, endpoint, null, null);
    }

    public APIResponse delete(Long id, String endpoint, Map<String, String> headers, Map<String, String> queryParams) {
        String url = ApiUtils.buildEndpointWithParams(endpoint + "/" + id, queryParams);
        RequestOptions options = ApiUtils.buildRequestOptions(null, headers, apiClient);
        return apiClient.execute(HttpMethod.DELETE, url, options);
    }

    // ---- Cleanup helper ----
    private final List<Long> createdEntities = new ArrayList<>();

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
}