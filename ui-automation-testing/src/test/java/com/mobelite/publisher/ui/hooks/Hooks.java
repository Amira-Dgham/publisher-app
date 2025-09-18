package com.mobelite.publisher.ui.hooks;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Tracing;
import com.mobelite.factory.PlaywrightFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

import java.nio.file.Paths;

public class Hooks {

    private Page page;

    @Before
    public void launchBrowser() {
        PlaywrightFactory.init();
        page = PlaywrightFactory.getPage();
    }

    // After runs in reverse order; order=0 runs first
    @After(order = 0)
    public void quitBrowser() {
        if (page != null) {
            page.close();
        }
        PlaywrightFactory.cleanup();
    }

    @After(order = 1)
    public void takeScreenshotAndTrace(Scenario scenario) {
        if (scenario.isFailed() && page != null) {
            String screenshotName = scenario.getName().replaceAll(" ", "_");
            byte[] screenshot = page.screenshot();
            scenario.attach(screenshot, "image/png", screenshotName);

            try {
                PlaywrightFactory.getContext().tracing().stop(
                        new Tracing.StopOptions()
                                .setPath(Paths.get("target/" + screenshotName + ".zip"))
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}