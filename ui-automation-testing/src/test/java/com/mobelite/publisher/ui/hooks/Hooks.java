package com.mobelite.publisher.ui.hooks;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Tracing;
import com.mobelite.factory.PlaywrightFactory;
import io.cucumber.java.*;
import io.qameta.allure.Allure;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Hooks {

    private Page page;

    /** Launch browser once before all scenarios */
    @BeforeAll
    public static void beforeAll() {
        PlaywrightFactory.launchBrowserOnce();
    }

    /** Create context & page per scenario */
    @Before(order = 0)
    public void beforeScenario() {
        PlaywrightFactory.initContextAndPage();
        page = PlaywrightFactory.getPage();
    }

    /** Take screenshot & trace on failure */
    @After(order = 0)
    public void afterScenarioCapture(Scenario scenario) {
        if (scenario.isFailed() && page != null) {
            String scenarioName = scenario.getName().replaceAll(" ", "_");
            try {
                // Screenshot
                byte[] screenshot = page.screenshot();
                Allure.addAttachment(scenarioName + "_screenshot", new ByteArrayInputStream(screenshot));

                // Trace
                Path tracePath = Paths.get("target/" + scenarioName + ".zip");
                PlaywrightFactory.getContext().tracing().stop(new Tracing.StopOptions().setPath(tracePath));

                if (Files.exists(tracePath)) {
                    Allure.addAttachment(scenarioName + "_trace", "application/zip",
                            new ByteArrayInputStream(Files.readAllBytes(tracePath)), ".zip");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /** Cleanup context & page after each scenario */
    @After(order = 1)
    public void afterScenarioCleanup() {
        PlaywrightFactory.cleanupScenario();
    }

    /** Close browser once after all scenarios */
    @AfterAll
    public static void afterAll() {
        PlaywrightFactory.closeBrowser();
    }
}