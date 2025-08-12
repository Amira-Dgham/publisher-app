package com.mobelite.e2e.config;

import com.microsoft.playwright.*;
import com.microsoft.playwright.junit.Options;
import com.microsoft.playwright.junit.OptionsFactory;

import java.nio.file.Paths;
import java.util.Arrays;

public class PlaywrightConfig implements OptionsFactory {

    @Override
    public Options getOptions() {
        return new Options()
                // Configure browser launch options (headless, args, slowMo)
                .setLaunchOptions(new BrowserType.LaunchOptions()
                        .setHeadless(TestConfig.getInstance().isHeadless())
                        .setSlowMo(50)
                        .setArgs(Arrays.asList(
                                "--disable-gpu",
                                "--no-sandbox",
                                "--disable-dev-shm-usage"
                        )))
                // Configure context options (viewport, downloads, video)
                .setContextOptions(new Browser.NewContextOptions()
                        .setViewportSize(1920, 1080)
                        .setAcceptDownloads(true)
                        .setRecordVideoDir(Paths.get("test-results/videos")));
    }
}