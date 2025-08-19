package com.mobelite.e2e.api.tests;
import com.microsoft.playwright.APIRequestContext;
import com.mobelite.e2e.api.core.ApiClient;
import com.mobelite.e2e.api.endpoints.AuthorEndpoints;
import com.mobelite.e2e.extensions.ApiContextExtension;
import com.mobelite.e2e.api.fixtures.AuthorFixtures;
import com.mobelite.e2e.api.models.Author;
import com.mobelite.e2e.api.models.ApiResponse;
import com.mobelite.e2e.api.models.PageResponse;
import com.mobelite.e2e.api.models.request.AuthorRequest;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive E2E tests for the Author API including CRUD operations.
 * Uses improved cleanup mechanisms to prevent orphaned test data.
 */
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

    @BeforeEach
    void setUp(APIRequestContext apiRequestContext) {
        log.info("Setting up AuthorE2ETest");
        apiClient = new ApiClient(apiRequestContext);
        authorEndpoints = new AuthorEndpoints(apiClient);
        authorFixtures = new AuthorFixtures(apiClient);
    }

    @BeforeAll
    void setupSharedAuthor(APIRequestContext apiRequestContext) {
        log.info("Setting up shared author for all tests");
        apiClient = new ApiClient(apiRequestContext);
        authorEndpoints = new AuthorEndpoints(apiClient);
        authorFixtures = new AuthorFixtures(apiClient);

        try {
            AuthorRequest authorRequest = authorFixtures.createSharedAuthorRequest();
            sharedAuthor = authorEndpoints.createAuthorAndValidateStructure(authorRequest);
            authorFixtures.registerAuthorForCleanup(sharedAuthor);
            log.info("Shared author created with ID: {}", sharedAuthor.getId());
        } catch (Exception e) {
            log.error("Failed to create shared author", e);
            throw new RuntimeException("Failed to setup shared author", e);
        }
    }

    @AfterEach
    void tearDown() {
        log.info("Tearing down AuthorE2ETest - Current cleanup count: {}", authorFixtures.getCleanupCount());
        if (authorFixtures != null) {
            authorFixtures.cleanupAllTestAuthors();
        }
    }

    @AfterAll
    void tearDownAll() {
        log.info("Final cleanup - ensuring all test authors are deleted");
        if (authorFixtures != null) {
            authorFixtures.cleanupAllTestAuthors();
            // Force cleanup as safety net
            authorFixtures.forceCleanupTestAuthorsByPattern();
        }
    }

    // -------- CREATE TEST SCENARIOS -------- //

    @Test
    @DisplayName("Should create author with valid data")
    @Description("Test creating an author with all valid fields populated")
    @Story("Author Creation")
    void testCreateAuthorWithValidData() {
        AuthorRequest authorRequest = authorFixtures.createValidAuthorRequest();
        Author createdAuthor = authorFixtures.createAuthorAndRegisterForCleanup(authorRequest);

        assertNotNull(createdAuthor);
        assertNotNull(createdAuthor.getId());
        assertEquals(authorRequest.getName(), createdAuthor.getName());
        assertEquals(authorRequest.getBirthDate(), createdAuthor.getBirthDate());
        assertEquals(authorRequest.getNationality(), createdAuthor.getNationality());

        log.info("Successfully created author with ID: {}", createdAuthor.getId());
    }

    @Test
    @DisplayName("Should create author with minimal data")
    @Description("Test creating an author with only required fields")
    @Story("Author Creation")
    void testCreateAuthorWithMinimalData() {
        AuthorRequest authorRequest = authorFixtures.createMinimalAuthorRequest();
        Author createdAuthor = authorFixtures.createAuthorAndRegisterForCleanup(authorRequest);

        assertNotNull(createdAuthor);
        assertNotNull(createdAuthor.getId());
        assertEquals(authorRequest.getName(), createdAuthor.getName());

        log.info("Successfully created minimal author with ID: {}", createdAuthor.getId());
    }

    // -------- READ/RETRIEVE TEST SCENARIOS -------- //

    @Test
    @DisplayName("Should retrieve shared author by ID")
    @Description("Test retrieving a shared author by their unique identifier")
    @Story("Author Retrieval")
    void testGetAuthorById() {
        assertNotNull(sharedAuthor, "Shared author must exist");

        Author retrievedAuthor = authorEndpoints.getAuthorByIdAndValidateStructure(sharedAuthor.getId());

        assertNotNull(retrievedAuthor);
        assertEquals(sharedAuthor.getId(), retrievedAuthor.getId());
        assertEquals(sharedAuthor.getName(), retrievedAuthor.getName());
        assertEquals(sharedAuthor.getBirthDate(), retrievedAuthor.getBirthDate());
        assertEquals(sharedAuthor.getNationality(), retrievedAuthor.getNationality());
    }

    @Test
    @DisplayName("Should retrieve all authors with pagination")
    @Description("Test retrieving all authors with default pagination")
    @Story("Author Retrieval")
    void testGetAllAuthors() {
        authorFixtures.setupMultipleTestAuthors(3);

        PageResponse<Author> authorsPage = authorEndpoints.getAllAuthorsAndValidateStructure();

        assertNotNull(authorsPage);
        assertTrue(authorsPage.getTotalElements() >= 3);
        assertTrue(authorsPage.hasContent());
        assertFalse(authorsPage.isEmpty());
    }

    @Test
    @DisplayName("Should retrieve authors with custom pagination")
    @Description("Test retrieving authors with custom page, size, and sort parameters")
    @Story("Author Retrieval")
    void testGetAllAuthorsWithCustomPagination() {
        authorFixtures.setupMultipleTestAuthors(5);

        ApiResponse<PageResponse<Author>> response = authorEndpoints.getAllAuthorsWithPagination(0, 10, "name");

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertNotNull(response.getData());

        PageResponse<Author> authorsPage = response.getData();
        assertEquals(0, authorsPage.getNumber());
        assertEquals(10, authorsPage.getSize());
        assertTrue(authorsPage.getTotalElements() >= 5);
    }

    // -------- DELETE TEST SCENARIOS -------- //

    @Test
    @DisplayName("Should delete author successfully")
    @Description("Test deleting an existing author should return success")
    @Story("Author Deletion")
    void testDeleteAuthor() {
        // Create an author specifically for deletion test
        Author authorToDelete = authorFixtures.createAuthorForDeletionTest();

        assertNotNull(authorToDelete);
        assertNotNull(authorToDelete.getId());
        log.info("Created author for deletion test with ID: {}", authorToDelete.getId());

        // Delete the author
        ApiResponse<Void> deleteResponse = authorEndpoints.deleteAuthorAndValidateStructure(authorToDelete.getId());

        assertNotNull(deleteResponse);
        assertTrue(deleteResponse.isSuccess());
        assertNotNull(deleteResponse.getMessage());
        log.info("Successfully deleted author with ID: {}", authorToDelete.getId());

        // Verify author is actually deleted by trying to retrieve it
        ApiResponse<?> getResponse = authorEndpoints.getNonExistentAuthorAndValidateError(authorToDelete.getId());
        assertNotNull(getResponse);
        assertFalse(getResponse.isSuccess());

        // Note: Don't add to cleanup list since it's already deleted
    }

    @Test
    @DisplayName("Should delete multiple authors successfully")
    @Description("Test deleting multiple authors in sequence")
    @Story("Author Deletion")
    void testDeleteMultipleAuthors() {
        // Create multiple authors for deletion
        List<Author> authorsToDelete = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Author createdAuthor = authorFixtures.createAuthorForDeletionTest();
            authorsToDelete.add(createdAuthor);
            log.info("Created author #{} for deletion test with ID: {}", i + 1, createdAuthor.getId());
        }

        // Delete all authors
        for (Author author : authorsToDelete) {
            ApiResponse<Void> deleteResponse = authorEndpoints.deleteAuthorAndValidateStructure(author.getId());

            assertNotNull(deleteResponse);
            assertTrue(deleteResponse.isSuccess());
            log.info("Successfully deleted author with ID: {}", author.getId());
        }

        // Verify all authors are deleted
        for (Author author : authorsToDelete) {
            ApiResponse<?> getResponse = authorEndpoints.getNonExistentAuthorAndValidateError(author.getId());
            assertNotNull(getResponse);
            assertFalse(getResponse.isSuccess());
        }

        // Note: Don't add to cleanup list since they're already deleted
    }

    @Test
    @DisplayName("Should delete author and verify it's removed from listings")
    @Description("Test that deleted author no longer appears in author listings")
    @Story("Author Deletion")
    void testDeleteAuthorAndVerifyRemovedFromListings() {
        // Get initial count
        PageResponse<Author> initialAuthors = authorEndpoints.getAllAuthorsAndValidateStructure();
        long initialCount = initialAuthors.getTotalElements();

        // Create an author
        Author authorToDelete = authorFixtures.createAuthorForDeletionTest();
        log.info("Created author for deletion test with ID: {}", authorToDelete.getId());

        // Verify author exists in listings
        PageResponse<Author> authorsAfterCreation = authorEndpoints.getAllAuthorsAndValidateStructure();
        assertEquals(initialCount + 1, authorsAfterCreation.getTotalElements());

        // Delete the author
        ApiResponse<Void> deleteResponse = authorEndpoints.deleteAuthorAndValidateStructure(authorToDelete.getId());
        assertTrue(deleteResponse.isSuccess());
        log.info("Successfully deleted author with ID: {}", authorToDelete.getId());

        // Verify author is removed from listings
        PageResponse<Author> authorsAfterDeletion = authorEndpoints.getAllAuthorsAndValidateStructure();
        assertEquals(initialCount, authorsAfterDeletion.getTotalElements());

        // Double-check that the specific author is not in the list
        boolean authorStillExists = authorsAfterDeletion.getContent().stream()
                .anyMatch(author -> author.getId().equals(authorToDelete.getId()));
        assertFalse(authorStillExists, "Deleted author should not appear in author listings");
    }

    // -------- NEGATIVE DELETE TEST SCENARIOS -------- //

    @Test
    @DisplayName("Should fail to delete non-existent author")
    @Description("Test deleting a non-existent author should return 404 error")
    @Story("Author Deletion")
    void testDeleteNonExistentAuthor() {
        Long nonExistentId = 999999L;
        ApiResponse<?> errorResponse = authorEndpoints.deleteNonExistentAuthorAndValidateError(nonExistentId);

        assertNotNull(errorResponse);
        assertFalse(errorResponse.isSuccess());
        assertNotNull(errorResponse.getMessage());
        log.info("Correctly received error when trying to delete non-existent author ID: {}", nonExistentId);
    }

    @Test
    @DisplayName("Should fail to delete already deleted author")
    @Description("Test deleting an author twice should return error on second attempt")
    @Story("Author Deletion")
    void testDeleteAlreadyDeletedAuthor() {
        // Create an author
        Author authorToDelete = authorFixtures.createAuthorForDeletionTest();

        assertNotNull(authorToDelete);
        assertNotNull(authorToDelete.getId());
        log.info("Created author for double deletion test with ID: {}", authorToDelete.getId());

        // Delete the author first time - should succeed
        ApiResponse<Void> firstDeleteResponse = authorEndpoints.deleteAuthorAndValidateStructure(authorToDelete.getId());
        assertTrue(firstDeleteResponse.isSuccess());
        log.info("First deletion successful for author ID: {}", authorToDelete.getId());

        // Try to delete the same author again - should fail
        ApiResponse<?> secondDeleteResponse = authorEndpoints.deleteNonExistentAuthorAndValidateError(authorToDelete.getId());

        assertNotNull(secondDeleteResponse);
        assertFalse(secondDeleteResponse.isSuccess());
        assertNotNull(secondDeleteResponse.getMessage());
        log.info("Second deletion correctly failed for author ID: {}", authorToDelete.getId());

        // Note: Don't add to cleanup list since it's already deleted
    }

    // -------- NEGATIVE TEST SCENARIOS -------- //

    @Test
    @DisplayName("Should fail to create author with invalid data")
    @Description("Test creating an author with invalid data should return error")
    @Story("Author Creation Validation")
    void testCreateAuthorWithInvalidData() {
        AuthorRequest invalidRequest = authorFixtures.createInvalidAuthorRequest();
        ApiResponse<?> errorResponse = authorEndpoints.createAuthorWithInvalidDataAndValidateError(invalidRequest);

        assertNotNull(errorResponse);
        assertFalse(errorResponse.isSuccess());
        assertNotNull(errorResponse.getMessage());
    }

    @Test
    @DisplayName("Should fail to retrieve non-existent author")
    @Description("Test retrieving a non-existent author should return 404 error")
    @Story("Author Retrieval")
    void testGetNonExistentAuthor() {
        Long nonExistentId = 999999L;
        ApiResponse<?> errorResponse = authorEndpoints.getNonExistentAuthorAndValidateError(nonExistentId);

        assertNotNull(errorResponse);
        assertFalse(errorResponse.isSuccess());
        assertNotNull(errorResponse.getMessage());
    }


    // -------- DATA CONSISTENCY TEST -------- //

    @Test
    @DisplayName("Should maintain data consistency across operations")
    @Description("Test that author data remains consistent across create and retrieve operations")
    @Story("Author Data Consistency")
    void testDataConsistencyAcrossOperations() {
        AuthorRequest authorRequest = authorFixtures.createValidAuthorRequest();
        Author createdAuthor = null;

        try {
            createdAuthor = authorEndpoints.createAuthorAndValidateStructure(authorRequest);
            // Add to cleanup list immediately after creation
            authorFixtures.getCreatedAuthors().add(createdAuthor);

            Author retrievedAuthor = authorEndpoints.getAuthorByIdAndValidateStructure(createdAuthor.getId());

            assertEquals(createdAuthor.getId(), retrievedAuthor.getId());
            assertEquals(createdAuthor.getName(), retrievedAuthor.getName());
            assertEquals(createdAuthor.getBirthDate(), retrievedAuthor.getBirthDate());
            assertEquals(createdAuthor.getNationality(), retrievedAuthor.getNationality());

            log.info("Data consistency verified for author ID: {}", createdAuthor.getId());
        } catch (Exception e) {
            // If creation succeeded but validation failed, ensure cleanup
            if (createdAuthor != null && createdAuthor.getId() != null) {
                authorFixtures.getCreatedAuthors().add(createdAuthor);
            }
            throw e;
        }
    }
}