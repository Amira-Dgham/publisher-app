package com.mobelite.e2e.api.fixtures;

import com.mobelite.e2e.api.core.ApiClient;
import com.mobelite.e2e.api.endpoints.AuthorEndpoints;
import com.mobelite.e2e.api.models.Author;
import com.mobelite.e2e.api.models.request.AuthorRequest;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Simplified AuthorFixtures providing essential test data and setup/teardown methods for author E2E testing.
 */
@Slf4j
public class AuthorFixtures {

    private final AuthorEndpoints authorEndpoints;
    private final List<Author> createdAuthors;
    private final AtomicLong authorIdCounter;

    // Test data constants
    private static final String TEST_AUTHOR_PREFIX = "TEST_AUTHOR_";

    /**
     * Constructs AuthorFixtures with the provided ApiClient.
     *
     * @param apiClient the ApiClient instance
     */
    public AuthorFixtures(ApiClient apiClient) {
        this.authorEndpoints = new AuthorEndpoints(apiClient);
        this.createdAuthors = new ArrayList<>();
        this.authorIdCounter = new AtomicLong(1);
    }

    // -------- ESSENTIAL TEST DATA GENERATION -------- //

    /**
     * Creates a valid author request with all fields populated.
     *
     * @return a valid AuthorRequest
     */
    @Step("Create valid author request")
    public AuthorRequest createValidAuthorRequest() {
        return AuthorRequest.builder()
                .name(TEST_AUTHOR_PREFIX + "Valid_" + authorIdCounter.getAndIncrement())
                .birthDate(LocalDate.of(1980, 1, 1))
                .nationality("American")
                .build();
    }

    /**
     * Creates a valid author request with all fields populated.
     *
     * @return a valid AuthorRequest
     */
    @Step("Create valid author request")
    public AuthorRequest createSharedAuthorRequest() {
        return AuthorRequest.builder()
                .name(TEST_AUTHOR_PREFIX + "Shared_" + authorIdCounter.getAndIncrement())
                .birthDate(LocalDate.of(1980, 1, 1))
                .nationality("American")
                .build();
    }

    /**
     * Creates a minimal author request with only required fields.
     *
     * @return a minimal AuthorRequest
     */
    @Step("Create minimal author request")
    public AuthorRequest createMinimalAuthorRequest() {
        return AuthorRequest.builder()
                .name(TEST_AUTHOR_PREFIX + "Minimal_" + authorIdCounter.getAndIncrement())
                .build();
    }

    /**
     * Creates an invalid author request for negative testing.
     *
     * @return an invalid AuthorRequest
     */
    @Step("Create invalid author request")
    public AuthorRequest createInvalidAuthorRequest() {
        return AuthorRequest.builder()
                .name("") // Invalid: empty name
                .birthDate(LocalDate.now().plusDays(1)) // Invalid: future date
                .nationality("This is a very long nationality that exceeds the maximum allowed length of fifty characters")
                .build();
    }

    // -------- TEST SETUP AND TEARDOWN -------- //

    /**
     * Sets up multiple test authors for testing pagination and bulk operations.
     *
     * @param count the number of authors to create
     */
    @Step("Setup {count} test authors")
    public void setupMultipleTestAuthors(int count) {
        for (int i = 0; i < count; i++) {
            AuthorRequest request = createValidAuthorRequest();
            Author createdAuthor = authorEndpoints.createAuthorAndValidateStructure(request);
            createdAuthors.add(createdAuthor);
            log.info("Setup: Created test author with ID: {}", createdAuthor.getId());
        }
    }

    /**
     * Cleans up all created test authors.
     * This method is typically called in @AfterEach.
     */
    @Step("Cleanup all test authors")
    public void cleanupAllTestAuthors() {
        log.info("Cleanup: Starting cleanup of {} test authors", createdAuthors.size());

        for (Author author : createdAuthors) {
            try {
                authorEndpoints.deleteAuthorAndValidateStructure(author.getId());
                log.info("Cleanup: Successfully cleaned up author with ID: {}", author.getId());
            } catch (Exception e) {
                log.warn("Cleanup: Failed to cleanup author with ID {}: {}", author.getId(), e.getMessage());
            }
        }

        createdAuthors.clear();
        log.info("Cleanup: Completed cleanup of all test authors");
    }

    // -------- UTILITY METHODS -------- //

    /**
     * Gets the list of all created authors.
     *
     * @return the list of created authors
     */
    public List<Author> getCreatedAuthors() {
        return new ArrayList<>(createdAuthors);
    }


}