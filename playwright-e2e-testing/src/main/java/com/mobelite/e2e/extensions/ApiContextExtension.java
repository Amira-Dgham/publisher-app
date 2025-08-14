package com.mobelite.e2e.extensions;

import com.microsoft.playwright.*;
import com.mobelite.e2e.config.TestConfig;
import org.junit.jupiter.api.extension.*;

public class ApiContextExtension implements BeforeAllCallback, AfterAllCallback, ParameterResolver {

    private static Playwright playwright;
    private static APIRequestContext apiRequestContext;

    @Override
    public void beforeAll(ExtensionContext context) {
        if (playwright == null) {
            playwright = Playwright.create();
        }
        if (apiRequestContext == null) {
            TestConfig config = TestConfig.getInstance();
            apiRequestContext = playwright.request().newContext(
                    new APIRequest.NewContextOptions()
                            .setBaseURL(config.getApiBaseUrl())
                            .setTimeout(config.getTimeout())
                            .setExtraHTTPHeaders(java.util.Map.of(
                                    "Content-Type", "application/json",
                                    "Accept", "application/json",
                                    "User-Agent", "E2E-Test-Client/1.0"
                            ))
            );
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
}
