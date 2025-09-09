package com.mobelite.e2e.config;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import io.qameta.allure.Allure;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

import java.io.ByteArrayInputStream;

@Slf4j
public abstract class BaseTest {

    protected static BrowserContext context;
    protected static Page page;
    protected static APIRequestContext api;
    protected static TestConfig config = TestConfig.getInstance();

    protected static final String BASE_UI_URL = config.getWebBaseUrl();
    protected static final String BASE_API_URL = config.getApiBaseUrl();

    @BeforeAll
    static void globalSetup() {
        PlaywrightManager.initBrowser(config.isHeadless());

        // UI
        context = PlaywrightManager.createContext();
        page = context.newPage();

        // API
        api = PlaywrightManager.createApiRequestContext(BASE_API_URL);
        log.info("API Base URL: {}", BASE_API_URL);
    }

    @AfterAll
    static void globalTeardown() {
        if (context != null) context.close();
        if (api != null) api.dispose();
        PlaywrightManager.close();
    }


    protected void navigateTo(String relativePath) {
        page.navigate(BASE_UI_URL + relativePath);
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }
    protected void takeScreenshot(String name) {
        var screenshot = page.screenshot(
                new Page.ScreenshotOptions().setFullPage(true)
        );

        Allure.addAttachment(name, new ByteArrayInputStream(screenshot));
    }
}