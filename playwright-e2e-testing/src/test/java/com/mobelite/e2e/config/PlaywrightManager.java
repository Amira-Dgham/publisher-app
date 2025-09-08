package com.mobelite.e2e.config;

import com.microsoft.playwright.*;
import lombok.extern.slf4j.Slf4j;
import java.util.Arrays;
import java.util.Map;

@Slf4j
public class PlaywrightManager {

    private static Playwright playwright;
    private static Browser browser;

    private PlaywrightManager() {}

    /** Initialize browser only once per test suite */
    public static void initBrowser(boolean headless) {
        if (playwright == null) playwright = Playwright.create();
        if (browser == null) {
            browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(headless)
                    .setSlowMo(50)
                    .setArgs(Arrays.asList("--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage"))
            );
        }
    }

    /** Create UI context */
    public static BrowserContext createContext() {
        if (browser == null) throw new IllegalStateException("Browser not initialized.");
        return browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1920, 1080)
                .setAcceptDownloads(true)
                //.setRecordVideoDir(Paths.get("test-results/videos"))
                .setExtraHTTPHeaders(Map.of("User-Agent", "E2E-Web-Client/1.0"))
        );
    }

    /** Create fresh page */
    public static Page createPage() {
        return createContext().newPage();
    }

    /** Create API request context */
    public static APIRequestContext createApiRequestContext(String baseUrl) {
        if (playwright == null) throw new IllegalStateException("Playwright not initialized.");
        return playwright.request().newContext(new APIRequest.NewContextOptions()
                .setBaseURL(baseUrl)
                .setExtraHTTPHeaders(Map.of(
                        "Content-Type", "application/json",
                        "Accept", "application/json",
                        "User-Agent", "E2E-API-Client/1.0"
                ))
        );
    }

    /** Close everything */
    public static void close() {
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