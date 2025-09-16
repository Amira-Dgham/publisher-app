package com.mobelite.publisher.ui.base;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.mobelite.publisher.ui.config.ConfigManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.util.Arrays;
import java.util.Map;

@Slf4j
@Getter
public class BaseTest {
    protected static Playwright playwright;
    protected static Browser browser;
    protected BrowserContext context;
    protected Page page;
    protected static ConfigManager config = ConfigManager.getInstance();
    protected APIRequestContext api;

    @BeforeClass
    public void setupClass() {
        createPlaywrightAndBrowser();

        context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1920, 1080)
                .setAcceptDownloads(true)
                .setExtraHTTPHeaders(Map.of("User-Agent", "E2E-Web-Client/1.0"))
        );

        page = context.newPage();

        api = playwright.request().newContext(new APIRequest.NewContextOptions()
                .setBaseURL(config.getApiBaseUrl())
                .setExtraHTTPHeaders(Map.of(
                        "Content-Type", "application/json",
                        "Accept", "application/json",
                        "User-Agent", "E2E-API-Client/1.0"
                ))
        );
    }

    @AfterClass
    public void tearDownClass() {
        if (page != null) page.close();
        if (context != null) context.close();
        if (api != null) api.dispose();
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    private void createPlaywrightAndBrowser() {
        if (playwright == null) playwright = Playwright.create();
        if (browser == null) {
            browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(config.isHeadless())
                    .setSlowMo(50)
                    .setArgs(Arrays.asList("--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage"))
            );
        }
    }

    protected void navigateTo(String relativePath) {
        page.navigate(config.getUiBaseUrl() + relativePath);
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }
}