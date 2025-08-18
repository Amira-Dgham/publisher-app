package com.mobelite.e2e.api.extensions;

import com.microsoft.playwright.*;
import com.mobelite.e2e.config.TestConfig;
import org.junit.jupiter.api.extension.*;

import java.util.Map;

public class ApiContextExtension implements BeforeAllCallback, AfterAllCallback, ParameterResolver {

    private static Playwright playwright;
    private static APIRequestContext apiRequestContext;

    @Override
    public void beforeAll(ExtensionContext context) {
        if (playwright == null) {
            playwright = Playwright.create();
        }
        if (apiRequestContext == null) {
            apiRequestContext = createApiRequestContext(playwright);
        }
    }

    @Override
    public void afterAll(ExtensionContext context) {
        if (apiRequestContext != null) {
            apiRequestContext.dispose();
            apiRequestContext = null;
        }
        if (playwright != null) {
            playwright.close();
            playwright = null;
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType() == APIRequestContext.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return apiRequestContext;
    }

    /**
     * Creates a new APIRequestContext with standardized configuration.
     * This method centralizes the configuration logic to avoid duplication.
     *
     * @param playwright the Playwright instance
     * @return configured APIRequestContext
     */
    private static APIRequestContext createApiRequestContext(Playwright playwright) {
        TestConfig config = TestConfig.getInstance();
        return playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL(config.getApiBaseUrl())
                        .setTimeout(config.getTimeout())
                        .setExtraHTTPHeaders(getDefaultHeaders())
        );
    }

    /**
     * Returns the default HTTP headers for API requests.
     * Centralizes header configuration to avoid duplication across classes.
     *
     * @return map of default headers
     */
    public static Map<String, String> getDefaultHeaders() {
        return Map.of(
                "Content-Type", "application/json",
                "Accept", "application/json",
                "User-Agent", "E2E-Test-Client/1.0"
        );
    }

    /**
     * Utility method to create a new APIRequestContext.
     * Can be used by other classes that need to create their own context.
     *
     * @param playwright the Playwright instance
     * @return configured APIRequestContext
     */
    public static APIRequestContext createNewContext(Playwright playwright) {
        return createApiRequestContext(playwright);
    }
}