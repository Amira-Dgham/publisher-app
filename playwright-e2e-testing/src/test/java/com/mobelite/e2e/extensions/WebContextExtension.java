package com.mobelite.e2e.extensions;

import com.microsoft.playwright.*;
import com.mobelite.e2e.config.TestConfig;
import org.junit.jupiter.api.extension.*;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;

/**
 * JUnit 5 extension to provide a Playwright Browser and BrowserContext
 * for web E2E tests, with automatic setup and teardown.
 */
public class WebContextExtension implements BeforeAllCallback, AfterAllCallback, ParameterResolver {

    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(WebContextExtension.class);

    @Override
    public void beforeAll(ExtensionContext context) {
        TestConfig config = TestConfig.getInstance();

        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(config.isHeadless())
                .setSlowMo(50)
                .setArgs(Arrays.asList(new String[]{
                        "--disable-gpu",
                        "--no-sandbox",
                        "--disable-dev-shm-usage"
                })));

        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                .setViewportSize(1920, 1080)
                .setAcceptDownloads(true)
                .setRecordVideoDir(Paths.get("test-results/videos"))
                .setExtraHTTPHeaders(Map.of("User-Agent", "E2E-Web-Client/1.0"));

        BrowserContext browserContext = browser.newContext(contextOptions);

        // Store objects in ExtensionContext for later retrieval
        context.getStore(NAMESPACE).put("playwright", playwright);
        context.getStore(NAMESPACE).put("browser", browser);
        context.getStore(NAMESPACE).put("context", browserContext);
    }

    @Override
    public void afterAll(ExtensionContext context) {
        BrowserContext browserContext = context.getStore(NAMESPACE).remove("context", BrowserContext.class);
        if (browserContext != null) browserContext.close();

        Browser browser = context.getStore(NAMESPACE).remove("browser", Browser.class);
        if (browser != null) browser.close();

        Playwright playwright = context.getStore(NAMESPACE).remove("playwright", Playwright.class);
        if (playwright != null) playwright.close();
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        Class<?> type = parameterContext.getParameter().getType();
        return type == BrowserContext.class || type == Browser.class || type == Playwright.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        Class<?> type = parameterContext.getParameter().getType();
        if (type == BrowserContext.class) {
            return extensionContext.getStore(NAMESPACE).get("context", BrowserContext.class);
        } else if (type == Browser.class) {
            return extensionContext.getStore(NAMESPACE).get("browser", Browser.class);
        } else if (type == Playwright.class) {
            return extensionContext.getStore(NAMESPACE).get("playwright", Playwright.class);
        }
        return null;
    }
}