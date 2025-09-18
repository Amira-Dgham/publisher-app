package com.mobelite.factory;

import com.microsoft.playwright.*;
import com.mobelite.config.ConfigManager;

import java.util.Arrays;

public class PlaywrightFactory {

    private static final ThreadLocal<Playwright> threadLocalPlaywright =
            ThreadLocal.withInitial(() -> {
                return Playwright.create();
                    }
            );

    private static final ThreadLocal<Browser> threadLocalBrowser = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> threadLocalContext = new ThreadLocal<>();
    private static final ThreadLocal<Page> threadLocalPage = new ThreadLocal<>();

    private static final ConfigManager config = ConfigManager.getInstance();

    public static void init() {
        boolean headless = config.isHeadless();
        String browserName = config.getBrowserName().toLowerCase();
        Browser browser;

        switch (browserName) {
            case "firefox":
                browser = threadLocalPlaywright.get().firefox()
                        .launch(new BrowserType.LaunchOptions().setHeadless(headless));
                break;
            case "chrome":
                browser = threadLocalPlaywright.get().chromium()
                        .launch(new BrowserType.LaunchOptions()
                                .setHeadless(headless)
                                .setSlowMo(500)
                                .setArgs(Arrays.asList("--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage")));
                break;
            case "webkit":
                browser = threadLocalPlaywright.get().webkit()
                        .launch(new BrowserType.LaunchOptions().setHeadless(headless)
                                .setSlowMo(500) // slows down actions for debugging
);
                break;
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browserName);
        }

        threadLocalBrowser.set(browser);

        BrowserContext context = browser.newContext();
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(false));
        threadLocalContext.set(context);

        Page page = context.newPage();
        threadLocalPage.set(page);
    }

    public static Page getPage() {
        return threadLocalPage.get();
    }

    public static BrowserContext getContext() {
        return threadLocalContext.get();
    }

    public static Browser getBrowser() {
        return threadLocalBrowser.get();
    }

    public static void cleanup() {
        if (threadLocalContext.get() != null) {
            threadLocalContext.get().close();
            threadLocalContext.remove();
        }
        if (threadLocalBrowser.get() != null) {
            threadLocalBrowser.get().close();
            threadLocalBrowser.remove();
        }
        if (threadLocalPlaywright.get() != null) {
            threadLocalPlaywright.get().close();
            threadLocalPlaywright.remove();
        }
        threadLocalPage.remove();
    }
}