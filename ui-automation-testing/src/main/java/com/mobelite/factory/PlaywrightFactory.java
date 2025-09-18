package com.mobelite.factory;

import com.microsoft.playwright.*;
import com.mobelite.config.ConfigManager;

import java.util.Arrays;
import java.util.Map;

public class PlaywrightFactory {

    private static Playwright playwright;
    private static Browser browser;

    private static final ThreadLocal<BrowserContext> threadLocalContext = new ThreadLocal<>();
    private static final ThreadLocal<Page> threadLocalPage = new ThreadLocal<>();

    private static final ConfigManager config = ConfigManager.getInstance();
    private static APIRequestContext apiRequestContext;
    private static final String BASE_API_URL = config.getApiBaseUrl();

    /** Launch browser once per suite */
    public static void launchBrowserOnce() {
        if (playwright == null) {
            playwright = Playwright.create();
        }
        if (browser == null) {
            boolean headless = config.isHeadless();
            String browserName = config.getBrowserName().toLowerCase();

            switch (browserName) {
                case "firefox":
                    browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(headless));
                    break;
                case "chrome":
                    browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                            .setHeadless(headless)
                            .setSlowMo(500)
                            .setArgs(Arrays.asList("--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage")));
                    break;
                case "webkit":
                    browser = playwright.webkit().launch(new BrowserType.LaunchOptions()
                            .setHeadless(headless)
                            .setSlowMo(500));
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported browser: " + browserName);
            }
        }
    }

    /** Create new context & page per scenario */
    public static void initContextAndPage() {
        BrowserContext context = browser.newContext();
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(false));
        threadLocalContext.set(context);

        Page page = context.newPage();
        threadLocalPage.set(page);
    }

    /** Create API request context */
    public static void createAPIRequestContext() {
        if (playwright == null) throw new IllegalStateException("Playwright not initialized.");
        if (apiRequestContext == null) {
            apiRequestContext = playwright.request().newContext(
                    new APIRequest.NewContextOptions()
                            .setBaseURL(BASE_API_URL)
                            .setExtraHTTPHeaders(Map.of(
                                    "Content-Type", "application/json",
                                    "Accept", "application/json",
                                    "User-Agent", "E2E-API-Client/1.0"
                            ))
            );
        }
    }

    public static APIRequestContext getApiRequestContext() {
        if (apiRequestContext == null) createAPIRequestContext();
        return apiRequestContext;
    }

    public static Page getPage() {
        return threadLocalPage.get();
    }

    public static BrowserContext getContext() {
        return threadLocalContext.get();
    }

    /** Navigate to any relative URL */
    public static void navigateTo(String relativeUrl) {
        Page page = getPage();
        if (page == null) throw new IllegalStateException("Page not initialized");
        page.navigate(ConfigManager.getInstance().getUiBaseUrl() + relativeUrl);
    }

    /** Cleanup context & page after scenario */
    public static void cleanupScenario() {
        if (threadLocalContext.get() != null) {
            threadLocalContext.get().close();
            threadLocalContext.remove();
        }
        threadLocalPage.remove();
    }

    /** Close browser at the very end of suite */
    public static void closeBrowser() {
        if (browser != null) {
            browser.close();
            browser = null;
        }
        if (playwright != null) {
            playwright.close();
            playwright = null;
        }
    }
}