package com.mobelite.publisher.api.base;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.mobelite.publisher.api.config.ConfigManager;
import com.mobelite.publisher.api.listeners.ApiFailureListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;

import java.util.Map;

@Slf4j
@Getter
@Listeners(ApiFailureListener.class)
public class BaseTest {
    private static Playwright playwright;
    protected APIRequestContext request;
    protected static ConfigManager config = ConfigManager.getInstance();
    protected static final String BASE_API_URL = config.getApiBaseUrl();
    // For listener
    private String lastRequest;
    private String lastResponse;
    // Allow ApiClient to update last request/response
    protected void setLastRequest(String lastRequest) {
        this.lastRequest = lastRequest;
    }

    protected void setLastResponse(String lastResponse) {
        this.lastResponse = lastResponse;
    }

    @BeforeClass
    public void setup() {
        createPlaywright();
        createAPIRequestContext();
    }

    @AfterClass
    public void tearDown() {
        disposeAPIRequestContext();
        closePlaywright();
    }

    private void createPlaywright() {
        playwright = Playwright.create();
    }

    private void createAPIRequestContext() {
        if (playwright == null) throw new IllegalStateException("Playwright not initialized.");
        this.request = playwright.request().newContext(new APIRequest.NewContextOptions()
                .setBaseURL(BASE_API_URL)
                .setExtraHTTPHeaders(Map.of(
                        "Content-Type", "application/json",
                        "Accept", "application/json",
                        "User-Agent", "E2E-API-Client/1.0"
                ))
        );
    }

    private void closePlaywright() {
        if (playwright != null) {
            playwright.close();
            playwright = null;
        }
    }

    private void disposeAPIRequestContext() {
        if (this.request != null) {
            this.request.dispose();
            this.request = null;
        }
    }

    protected void logResponse(final APIResponse response) {
        log.info("Status: {}", response.status());
        try {
            log.info("Body: {}", response.text()); // Logs response body as string
        } catch (Exception e) {
            log.error("Failed to read response body", e);
        }
    }
}