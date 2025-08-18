package com.mobelite.e2e.api.clients;

import com.mobelite.e2e.api.core.ApiClient;
import com.mobelite.e2e.api.endpoints.BaseEndpoints;

public class BookApiClient extends BaseEndpoints {

    /**
     * Constructs the BaseEndpoints with a provided ApiClient.
     *
     * @param apiClient the ApiClient instance to be used for sending requests
     */
    protected BookApiClient(ApiClient apiClient) {
        super(apiClient);
    }
}