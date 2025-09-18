package com.mobelite.publisher.ui.hooks;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Tracing;
import com.mobelite.factory.PlaywrightFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Hooks {

    private Page page;

    @Before
    public void launchBrowser() {
        PlaywrightFactory.init();
        page = PlaywrightFactory.getPage();
    }

    @After(order = 1)
    public void quitBrowser() {
        if (page != null) {
            page.close();
        }
        PlaywrightFactory.cleanup();
    }

    @After(order = 0) // run BEFORE quitBrowser to capture screenshots/traces
    public void takeScreenshotAndTrace(Scenario scenario) {
        if (scenario.isFailed() && page != null) {
            String scenarioName = scenario.getName().replaceAll(" ", "_");
            try {
                // Screenshot
                byte[] screenshot = page.screenshot();
                Allure.addAttachment(scenarioName + "_screenshot", new ByteArrayInputStream(screenshot));

                // Trace
                Path tracePath = Paths.get("target/" + scenarioName + ".zip");
                PlaywrightFactory.getContext().tracing().stop(
                        new Tracing.StopOptions().setPath(tracePath)
                );

                if (Files.exists(tracePath)) {
                    Allure.addAttachment(scenarioName + "_trace", "application/zip",
                            new ByteArrayInputStream(Files.readAllBytes(tracePath)), ".zip");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}