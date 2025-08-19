package com.mobelite.e2e;

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
            // Display test information
            log.info("Use Maven to run tests: mvn test");

        } catch (Exception e) {
            log.error("Failed to initialize E2E testing framework: {}", e.getMessage(), e);
            System.exit(1);
        }
    }
}