package com.mobelite.e2e;

import com.mobelite.e2e.config.TestConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * Main class for running Author E2E tests.
 * Provides a simple entry point for test execution.
 */
@Slf4j
public class Main {

    public static void main(String[] args) {
        log.info("Starting Author E2E Testing Framework");

        try {
            // Load test configuration
            TestConfig config = TestConfig.getInstance();
            log.info("Test configuration loaded - API Base URL: {}", config.getApiBaseUrl());

            // Display test information
            log.info("Author E2E Tests are ready to run");
            log.info("Use Maven to run tests: mvn test");
            log.info("Or run individual test classes directly");

        } catch (Exception e) {
            log.error("Failed to initialize E2E testing framework: {}", e.getMessage(), e);
            System.exit(1);
        }
    }
}