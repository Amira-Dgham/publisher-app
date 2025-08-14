package com.mobelite.e2e.api.client.api;

import com.mobelite.e2e.api.client.ApiClient;
import com.mobelite.e2e.api.endpoints.BaseEndpoints;

public class PublicationApiClient extends BaseEndpoints {
    /**
     * Constructs the BaseEndpoints with a provided ApiClient.
     *
     * @param apiClient the ApiClient instance to be used for sending requests
     */
    protected PublicationApiClient(ApiClient apiClient) {
        super(apiClient);
    }
}
