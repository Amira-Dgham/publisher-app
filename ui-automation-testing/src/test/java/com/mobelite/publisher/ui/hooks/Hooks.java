package com.mobelite.publisher.ui.hooks;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Tracing;
import com.mobelite.factory.PlaywrightFactory;
import com.mobelite.models.Author;
import com.mobelite.models.request.AuthorRequest;
import com.mobelite.utils.ApiUtils;
import com.mobelite.models.response.ApiResponse;
import com.mobelite.publisher.ui.test_data.AuthorTestDataFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import io.cucumber.java.*;
import io.qameta.allure.Allure;
import lombok.Getter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.mobelite.publisher.ui.constants.ApiEndpoints.AUTHORS_BASE;

public class Hooks {

    private Page page;

    @Getter
    private static Long authorId;
    private static final AtomicBoolean authorCreated = new AtomicBoolean(false);
    private static final List<Long> createdAuthors = new ArrayList<>();
    private static ApiUtils apiUtils;
    private static final TypeReference<ApiResponse<Author>> typeRef = new TypeReference<>() {};

    /** Launch browser and create author once before all scenarios */
    @BeforeAll
    public static void beforeAll() {
        try {
            PlaywrightFactory.launchBrowserOnce();

            apiUtils = new ApiUtils(PlaywrightFactory.getApiRequestContext());

            if (!authorCreated.get()) {
                AuthorRequest authorRequest = AuthorTestDataFactory.createValidAuthorRequest();
                ApiResponse<Author> response = apiUtils.post(
                        AUTHORS_BASE,
                        authorRequest,
                        typeRef
                );
                authorId = response.getData().getId();
                createdAuthors.add(authorId);
                authorCreated.set(true);
                System.out.println("Created author with ID: " + authorId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /** Create context & page per scenario */
    @Before(order = 0)
    public void beforeScenario() {
        PlaywrightFactory.initContextAndPage();
        page = PlaywrightFactory.getPage();
    }

    /** Capture screenshot and trace on failure */
    @After(order = 0)
    public void afterScenarioCapture(Scenario scenario) {
        if (scenario.isFailed() && page != null) {
            String scenarioName = scenario.getName().replaceAll(" ", "_");

            try {
                // --- Screenshot ---
                byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
                saveScreenshotLocally(scenarioName, screenshot);
                Allure.addAttachment(scenarioName + "_screenshot",
                        "image/png",
                        new ByteArrayInputStream(screenshot),
                        ".png");

                // --- Tracing ---
                Path tracePath = Paths.get("target/traces/" + scenarioName + ".zip");
                Files.createDirectories(tracePath.getParent());
                PlaywrightFactory.getContext().tracing().stop(
                        new Tracing.StopOptions().setPath(tracePath)
                );

                if (Files.exists(tracePath)) {
                    Allure.addAttachment(scenarioName + "_trace",
                            "application/zip",
                            new ByteArrayInputStream(Files.readAllBytes(tracePath)),
                            ".zip");
                }

            } catch (Exception e) {
                System.err.println("Failed to capture failure artifacts for scenario: " + scenarioName);
                e.printStackTrace();
            }
        }
    }

    @After(order = 1)
    public void afterScenarioCleanup() {
        PlaywrightFactory.cleanupScenario();
    }

    /** Delete all dynamically created authors once after all scenarios */
    @AfterAll
    public static void afterAll() {
        System.out.println("=== Cleanup: Deleting dynamically created authors ===");

        for (Long id : new ArrayList<>(createdAuthors)) {
            try {
                if (id != null) {
                    apiUtils.deleteById(AUTHORS_BASE, id);
                    System.out.printf("[%s] Deleted author (ID: %d)%n",
                            java.time.LocalDateTime.now(), id);
                } else {
                    System.out.printf("[%s]  Author ID is null, cannot delete%n",
                            java.time.LocalDateTime.now());
                }
            } catch (Exception e) {
                System.err.printf("[%s] Failed to delete author (ID: %d) - %s%n",
                        java.time.LocalDateTime.now(), id, e.getMessage());
            }
        }

        createdAuthors.clear();

        // Close browser at the end
        System.out.println("Closing Playwright browser...");
        PlaywrightFactory.closeBrowser();
        System.out.println("=== Cleanup completed ===");
    }

    /** Utility method to save screenshot locally */
    private void saveScreenshotLocally(String scenarioName, byte[] screenshot) {
        try {
            Path path = Paths.get("target/screenshots/" + scenarioName + ".png");
            Files.createDirectories(path.getParent());
            Files.write(path, screenshot);
        } catch (IOException e) {
            System.err.println("Failed to save screenshot locally for scenario: " + scenarioName);
            e.printStackTrace();
        }
    }

    /** Lazy getter for API utils */
    private ApiUtils getApiUtils() {
        if (apiUtils == null) {
            apiUtils = new ApiUtils(PlaywrightFactory.getApiRequestContext());
        }
        return apiUtils;
    }
}