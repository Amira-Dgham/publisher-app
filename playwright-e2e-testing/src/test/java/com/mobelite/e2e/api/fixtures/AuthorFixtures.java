package com.mobelite.e2e.api.fixtures;

import com.mobelite.e2e.api.core.ApiClient;
import com.mobelite.e2e.api.endpoints.AuthorEndpoints;
import com.mobelite.e2e.api.models.Author;
import com.mobelite.e2e.api.models.PageResponse;
import com.mobelite.e2e.api.models.request.AuthorRequest;
import io.qameta.allure.Step;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Simplified AuthorFixtures providing essential test data and setup/teardown methods for author E2E testing.
 */
@Slf4j
public class AuthorFixtures {

    private final AuthorEndpoints authorEndpoints;
    /**
     * -- GETTER --
     *  Gets the list of all created authors.
     *  Returns a defensive copy to prevent external modification.
     *
     * @return a copy of the list of created authors
     */ // Return the actual list for adding authors
    @Getter
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
        this.createdAuthors = Collections.synchronizedList(new ArrayList<>());
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
                .name(TEST_AUTHOR_PREFIX + "Valid_" + UUID.randomUUID())
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

    // -------- IMPROVED TEST SETUP AND TEARDOWN -------- //

    /**
     * Sets up multiple test authors for testing pagination and bulk operations.
     * Each created author is immediately added to the cleanup list.
     *
     * @param count the number of authors to create
     */
    @Step("Setup {count} test authors")
    public void setupMultipleTestAuthors(int count) {
        log.info("Setting up {} test authors", count);

        for (int i = 0; i < count; i++) {
            Author createdAuthor = null;
            try {
                AuthorRequest request = createValidAuthorRequest();
                createdAuthor = authorEndpoints.createAuthorAndValidateStructure(request);
                // Add to cleanup list immediately after creation
                createdAuthors.add(createdAuthor);
                log.info("Setup: Created test author #{} with ID: {}", i + 1, createdAuthor.getId());
            } catch (Exception e) {
                log.error("Failed to create test author #{}: {}", i + 1, e.getMessage());
                // If author was created but validation failed, still add to cleanup
                if (createdAuthor != null && createdAuthor.getId() != null) {
                    createdAuthors.add(createdAuthor);
                }
                // Continue with next author instead of failing completely
            }
        }

        log.info("Setup completed. Total authors to cleanup: {}", createdAuthors.size());
    }

    /**
     * Cleans up all created test authors with improved error handling and logging.
     * This method is typically called in @AfterEach and @AfterAll.
     */
    @Step("Cleanup all test authors")
    public void cleanupAllTestAuthors() {
        if (createdAuthors.isEmpty()) {
            log.info("No test authors to cleanup");
            return;
        }

        log.info("Cleanup: Starting cleanup of {} test authors", createdAuthors.size());

        List<Author> failedDeletes = new ArrayList<>();
        int successCount = 0;

        // Create a copy to avoid concurrent modification
        List<Author> authorsToDelete = new ArrayList<>(createdAuthors);

        for (Author author : authorsToDelete) {
            if (author == null || author.getId() == null) {
                log.warn("Cleanup: Skipping null author or author with null ID");
                continue;
            }

            try {
                authorEndpoints.deleteAuthorAndValidateStructure(author.getId());
                successCount++;
                log.info("Cleanup: Successfully deleted author with ID: {}", author.getId());
            } catch (Exception e) {
                log.error("Cleanup: Failed to delete author with ID {}: {}", author.getId(), e.getMessage());
                failedDeletes.add(author);
            }
        }

        // Clear the list regardless of success/failure
        createdAuthors.clear();

        log.info("Cleanup completed: {} successful, {} failed deletions", successCount, failedDeletes.size());

        // If there were failures, log them for investigation
        if (!failedDeletes.isEmpty()) {
            log.warn("Cleanup: Failed to delete the following author IDs: {}",
                    failedDeletes.stream().map(Author::getId).toList());
        }
    }

    /**
     * Force cleanup by attempting to delete all test authors by name pattern.
     * This is a more aggressive cleanup method that can be used if regular cleanup fails.
     */
    @Step("Force cleanup all test authors by pattern")
    public void forceCleanupTestAuthorsByPattern() {
        log.info("Force cleanup: Attempting to find and delete all test authors by name pattern");

        try {
            // Get all authors and filter by test prefix
            PageResponse<Author> allAuthors = authorEndpoints.getAllAuthorsAndValidateStructure();

            if (allAuthors != null && allAuthors.hasContent()) {
                List<Author> testAuthors = allAuthors.getContent().stream()
                        .filter(author -> author.getName() != null &&
                                author.getName().startsWith(TEST_AUTHOR_PREFIX))
                        .toList();

                log.info("Force cleanup: Found {} test authors to delete", testAuthors.size());

                for (Author author : testAuthors) {
                    try {
                        authorEndpoints.deleteAuthorAndValidateStructure(author.getId());
                        log.info("Force cleanup: Deleted test author with ID: {}", author.getId());
                    } catch (Exception e) {
                        log.error("Force cleanup: Failed to delete test author {}: {}", author.getId(), e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Force cleanup: Failed to retrieve authors for pattern-based cleanup", e);
        }
    }

    /**
     * Creates an author and immediately registers it for cleanup.
     * This is a convenience method to ensure all created authors are tracked.
     *
     * @param authorRequest the author creation request
     * @return the created author
     */
    @Step("Create author and register for cleanup")
    public Author createAuthorAndRegisterForCleanup(AuthorRequest authorRequest) {
        Author createdAuthor = authorEndpoints.createAuthorAndValidateStructure(authorRequest);
        registerAuthorForCleanup(createdAuthor);
        return createdAuthor;
    }

    /**
     * Creates an author specifically for deletion testing.
     * The author is NOT added to the cleanup list since it will be deleted in the test.
     *
     * @return a created author for deletion testing
     */
    @Step("Create author for deletion test")
    public Author createAuthorForDeletionTest() {
        AuthorRequest authorRequest = AuthorRequest.builder()
                .name(TEST_AUTHOR_PREFIX + "ForDeletion_" + UUID.randomUUID())
                .birthDate(LocalDate.of(1975, 6, 15))
                .nationality("British")
                .build();

        return authorEndpoints.createAuthorAndValidateStructure(authorRequest);
    }

    // -------- UTILITY METHODS -------- //

    /**
     * Registers an author for cleanup. Use this when creating authors outside of fixtures.
     *
     * @param author the author to register for cleanup
     */
    @Step("Register author for cleanup")
    public void registerAuthorForCleanup(Author author) {
        if (author != null && author.getId() != null) {
            createdAuthors.add(author);
            log.info("Registered author with ID {} for cleanup", author.getId());
        }
    }

    /**
     * Gets the current count of authors registered for cleanup.
     *
     * @return the number of authors in the cleanup list
     */
    public int getCleanupCount() {
        return createdAuthors.size();
    }
}