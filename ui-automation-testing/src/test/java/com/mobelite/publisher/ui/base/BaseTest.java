package com.mobelite.publisher.ui.base;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.mobelite.publisher.ui.config.ConfigManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.util.Arrays;
import java.util.Map;

@Slf4j
@Getter
public class BaseTest {
    protected Page page;
    protected BrowserContext context;
    private static Playwright playwright;
    private static Browser browser;
    protected static ConfigManager config = ConfigManager.getInstance();

    @BeforeClass
    public void setup() {
        createPlaywrightAndBrowser();
    }

    @BeforeMethod
    public void setupTest() {
        context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1920, 1080)
                .setAcceptDownloads(true)
                .setExtraHTTPHeaders(Map.of("User-Agent", "E2E-Web-Client/1.0"))
        );
        page = context.newPage();
    }

    @AfterMethod
    public void tearDownTest() {
        if (page != null) page.close();
        if (context != null) context.close();
    }

    @AfterClass
    public void tearDownClass() {
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