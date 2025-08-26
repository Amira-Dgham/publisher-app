package com.mobelite.e2e.api.tests;

import com.microsoft.playwright.APIRequestContext;
import com.mobelite.e2e.api.core.ApiAssertions;
import com.mobelite.e2e.api.core.ApiClient;
import com.mobelite.e2e.api.endpoints.AuthorEndpoints;
import com.mobelite.e2e.api.fixtures.AuthorFixtures;
import com.mobelite.e2e.api.models.Author;
import com.mobelite.e2e.api.models.ApiResponse;
import com.mobelite.e2e.api.models.PageResponse;
import com.mobelite.e2e.api.models.request.AuthorRequest;
import com.mobelite.e2e.extensions.ApiContextExtension;
import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Author Management")
@Feature("Author API")
@Story("E2E Testing")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Author API E2E Tests")
@Slf4j
@ExtendWith(ApiContextExtension.class)
public class AuthorE2ETest {

    private ApiClient apiClient;
    private AuthorEndpoints authorEndpoints;
    private AuthorFixtures authorFixtures;
    private Author sharedAuthor;

    @BeforeAll
    void initAll(APIRequestContext apiRequestContext) {
        log.info("Initializing AuthorE2ETest suite");
        apiClient = new ApiClient(apiRequestContext);
        authorEndpoints = new AuthorEndpoints(apiClient);
        authorFixtures = new AuthorFixtures(apiClient);

        // Create a shared author for all tests
        sharedAuthor = authorFixtures.createAuthorAndRegisterForCleanup(
                authorFixtures.createValidAuthorRequest()
        );
        log.info("Shared author created with ID: {}", sharedAuthor.getId());
    }

    @AfterEach
    void cleanupAfterEach() {
        log.info("Cleaning up per-test authors...");

        if (sharedAuthor != null) {
            authorFixtures.removeAuthorFromCleanup(sharedAuthor);
        }

        authorFixtures.cleanupAllAuthors(); // deletes only test-specific authors
    }

    @AfterAll
    void finalCleanup() {
        log.info("Final cleanup for all remaining authors");
        int remainingCount = authorFixtures.getCleanupCount();

        if (remainingCount > 0) {
            log.info("Attempting final cleanup of {} authors", remainingCount);
            List<Long> pendingIds = authorFixtures.getAuthorsPendingCleanup();
            log.debug("Final cleanup for author IDs: {}", pendingIds);

            authorFixtures.cleanupAllAuthors();

            // Force cleanup any remaining authors
            int stillRemaining = authorFixtures.getCleanupCount();
            if (stillRemaining > 0) {
                log.warn("Force cleaning {} remaining authors", stillRemaining);
                for (Long authorId : authorFixtures.getAuthorsPendingCleanup()) {
                    authorFixtures.forceCleanupAuthor(authorId);
                }
            }
        }

        log.info("Final cleanup complete. Remaining authors: {}", authorFixtures.getCleanupCount());
    }

    // -------- CREATE TESTS -------- //

    @Test
    @DisplayName("Create author with valid data")
    void createAuthorWithValidData() {
        AuthorRequest request = authorFixtures.createValidAuthorRequest();
        Author created = authorFixtures.createAuthorAndRegisterForCleanup(request);

        assertNotNull(created, "Created author should not be null");
        assertNotNull(created.getId(), "Created author ID should not be null");
        assertEquals(request.getName(), created.getName());
        assertEquals(request.getBirthDate(), created.getBirthDate());
        assertEquals(request.getNationality(), created.getNationality());

        log.info("Author created successfully: {}", created.getId());
    }

    @Test
    @DisplayName("Create author with minimal data")
    void createAuthorWithMinimalData() {
        AuthorRequest request = authorFixtures.createMinimalAuthorRequest();
        Author created = authorFixtures.createAuthorAndRegisterForCleanup(request);

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals(request.getName(), created.getName());

        log.info("Minimal author created successfully: {}", created.getId());
    }

    // -------- READ TESTS -------- //

    @Test
    @DisplayName("Retrieve shared author by ID")
    void getSharedAuthorById() {
        assertNotNull(sharedAuthor);
        assertNotNull(sharedAuthor.getId());

        Author retrieved = authorEndpoints.getAuthorByIdAndValidateStructure(sharedAuthor.getId());

        assertNotNull(retrieved);
        assertEquals(sharedAuthor.getId(), retrieved.getId());
        assertEquals(sharedAuthor.getName(), retrieved.getName());

        log.info("Successfully retrieved shared author: {}", retrieved.getId());
    }

    @Test
    @DisplayName("Retrieve authors with pagination")
    void getAllAuthorsWithPagination() {
        int testAuthorsCount = 5;
        log.info("Setting up {} test authors for pagination test", testAuthorsCount);

        authorFixtures.setupTestAuthors(testAuthorsCount);

        // Verify authors were created
        int actualCreated = authorFixtures.getCleanupCount();
        log.info("Actually created {} authors (including shared)", actualCreated);

        PageResponse<Author> page = authorEndpoints.getAllAuthorsAndValidateStructure();
        assertNotNull(page, "Page response should not be null");
        assertTrue(page.getTotalElements() >= testAuthorsCount,
                "Should have at least " + testAuthorsCount + " authors, but found: " + page.getTotalElements());
        assertTrue(page.hasContent(), "Page should have content");

        log.info("Pagination test successful - found {} total authors", page.getTotalElements());
    }

    // -------- DELETE TESTS -------- //

    @Test
    @DisplayName("Delete author successfully")
    void deleteAuthor() {
        // Create author specifically for deletion test
        Author author = authorFixtures.createAuthorAndRegisterForCleanup(
                authorFixtures.createValidAuthorRequest()
        );

        assertNotNull(author, "Test author should be created");
        assertNotNull(author.getId(), "Test author ID should not be null");

        log.info("Deleting author: {}", author.getId());

        // Perform deletion
        ApiResponse<Void> response = authorEndpoints.deleteAuthorAndValidateStructure(author.getId());
        assertTrue(response.isSuccess(), "Delete operation should succeed");

        // Remove from cleanup list since we manually deleted it
        authorFixtures.removeAuthorFromCleanup(author);

        // Verify author no longer exists
        ApiResponse<?> getResponse = authorEndpoints.getNonExistentAuthorAndValidateError(author.getId());
        assertFalse(getResponse.isSuccess(), "Get operation should fail for deleted author");

        log.info("Delete test successful for author: {}", author.getId());
    }

    @Test
    @DisplayName("Fail to delete non-existent author")

    void deleteNonExistentAuthor() {
        Long nonExistentId = 999999L;
        log.info("Attempting to delete non-existent author: {}", nonExistentId);

        ApiResponse<?> response = authorEndpoints.deleteNonExistentAuthorAndValidateError(nonExistentId);
        assertFalse(response.isSuccess());
        assertNotNull(response.getMessage(), "Author not found ");

        log.info("Delete non-existent author test successful - got expected error: {}", response.getMessage());
    }

    // -------- NEGATIVE TESTS -------- //

    @Test
    @DisplayName("Fail to create author with invalid data")
    void createAuthorWithInvalidData() {
        AuthorRequest invalid = authorFixtures.createInvalidAuthorRequest();
        log.info("Attempting to create author with invalid data");

        ApiResponse<?> response = authorEndpoints.createAuthorWithInvalidDataAndValidateError(invalid);

        assertFalse(response.isSuccess());
        assertNotNull(response.getMessage(), "Validation failed");

        log.info("Invalid data test successful - got expected error: {}", response.getMessage());
    }

    // -------- DATA CONSISTENCY -------- //

    @Test
    @DisplayName("Data consistency across create and retrieve")
    void dataConsistency() {
        AuthorRequest request = authorFixtures.createValidAuthorRequest();
        Author created = authorFixtures.createAuthorAndRegisterForCleanup(request);

        assertNotNull(created);
        assertNotNull(created.getId());

        Author retrieved = authorEndpoints.getAuthorByIdAndValidateStructure(created.getId());

        assertNotNull(retrieved);
        assertEquals(created.getId(), retrieved.getId());
        assertEquals(created.getName(), retrieved.getName());
        assertEquals(created.getBirthDate(), retrieved.getBirthDate());

        log.info("Data consistency test successful for author: {}", created.getId());
    }
}