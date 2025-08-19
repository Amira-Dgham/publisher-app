package com.mobelite.e2e.extensions;

import com.microsoft.playwright.*;
import com.mobelite.e2e.config.TestConfig;
import org.junit.jupiter.api.extension.*;

import java.util.Map;

/**
 * JUnit 5 extension to provide a Playwright APIRequestContext
 * for API E2E tests, with automatic setup and teardown.
 */
public class ApiContextExtension implements BeforeAllCallback, AfterAllCallback, ParameterResolver {

    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ApiContextExtension.class);

    @Override
    public void beforeAll(ExtensionContext context) {
        TestConfig config = TestConfig.getInstance();
        Playwright playwright = Playwright.create();

        APIRequestContext apiRequestContext = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL(config.getApiBaseUrl())
                        .setTimeout(config.getTimeout())
                        .setExtraHTTPHeaders(Map.of(
                                "Content-Type", "application/json",
                                "Accept", "application/json",
                                "User-Agent", "E2E-API-Client/1.0"
                        ))
        );

        // Store objects in ExtensionContext for later retrieval
        context.getStore(NAMESPACE).put("playwright", playwright);
        context.getStore(NAMESPACE).put("apiContext", apiRequestContext);
    }

    @Override
    public void afterAll(ExtensionContext context) {
        APIRequestContext apiRequestContext = context.getStore(NAMESPACE).remove("apiContext", APIRequestContext.class);
        if (apiRequestContext != null) apiRequestContext.dispose();

        Playwright playwright = context.getStore(NAMESPACE).remove("playwright", Playwright.class);
        if (playwright != null) playwright.close();
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        Class<?> type = parameterContext.getParameter().getType();
        return type == APIRequestContext.class || type == Playwright.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        Class<?> type = parameterContext.getParameter().getType();
        if (type == APIRequestContext.class) {
            return extensionContext.getStore(NAMESPACE).get("apiContext", APIRequestContext.class);
        } else if (type == Playwright.class) {
            return extensionContext.getStore(NAMESPACE).get("playwright", Playwright.class);
        }
        return null;
    }
}