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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Focused E2E tests for the Author API.
 * Tests only essential CRUD operations and validation scenarios.
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

    @BeforeEach
    void setUp(APIRequestContext apiRequestContext) {
        log.info("Setting up AuthorE2ETest");
        apiClient = new ApiClient(apiRequestContext);
        authorEndpoints = new AuthorEndpoints(apiClient);
        authorFixtures = new AuthorFixtures(apiClient);
    }

    @AfterEach
    void tearDown() {
        log.info("Tearing down AuthorE2ETest");
        if (authorFixtures != null) {
            authorFixtures.cleanupAllTestAuthors();
        }
    }


    // -------- POSITIVE TEST SCENARIOS -------- //

    @Test
    @DisplayName("Should create author with valid data")
    @Description("Test creating an author with all valid fields populated")
    @Story("Author Creation")
    void testCreateAuthorWithValidData() {
        // Arrange
        AuthorRequest authorRequest = authorFixtures.createValidAuthorRequest();

        // Act
        Author createdAuthor = authorEndpoints.createAuthorAndValidateStructure(authorRequest);

        // Assert
        assertNotNull(createdAuthor);
        assertNotNull(createdAuthor.getId());
        assertEquals(authorRequest.getName(), createdAuthor.getName());
        assertEquals(authorRequest.getBirthDate(), createdAuthor.getBirthDate());
        assertEquals(authorRequest.getNationality(), createdAuthor.getNationality());

        // Store for cleanup
        authorFixtures.getCreatedAuthors().add(createdAuthor);
    }

    @Test
    @DisplayName("Should create author with minimal data")
    @Description("Test creating an author with only required fields")
    @Story("Author Creation")
    void testCreateAuthorWithMinimalData() {
        // Arrange
        AuthorRequest authorRequest = authorFixtures.createMinimalAuthorRequest();

        // Act
        Author createdAuthor = authorEndpoints.createAuthorAndValidateStructure(authorRequest);

        // Assert
        assertNotNull(createdAuthor);
        assertNotNull(createdAuthor.getId());
        assertEquals(authorRequest.getName(), createdAuthor.getName());

        // Store for cleanup
        authorFixtures.getCreatedAuthors().add(createdAuthor);
    }

    @Test
    @DisplayName("Should retrieve author by ID")
    @Description("Test retrieving an author by their unique identifier")
    @Story("Author Retrieval")
    void testGetAuthorById() {
        // Arrange: Create an author first
        AuthorRequest authorRequest = authorFixtures.createValidAuthorRequest();
        Author createdAuthor = authorEndpoints.createTestAuthorViaEndpoint(authorRequest);
        authorFixtures.getCreatedAuthors().add(createdAuthor);

        // Act: Retrieve the author by ID
        Author retrievedAuthor = authorEndpoints.getAuthorByIdAndValidateStructure(createdAuthor.getId());

        // Assert: Validate the retrieved data
        assertNotNull(retrievedAuthor);
        assertEquals(createdAuthor.getId(), retrievedAuthor.getId());
        assertEquals(createdAuthor.getName(), retrievedAuthor.getName());
        assertEquals(createdAuthor.getBirthDate(), retrievedAuthor.getBirthDate());
        assertEquals(createdAuthor.getNationality(), retrievedAuthor.getNationality());
    }

    @Test
    @DisplayName("Should retrieve all authors with pagination")
    @Description("Test retrieving all authors with default pagination")
    @Story("Author Retrieval")
    void testGetAllAuthors() {
        // Arrange: Create multiple authors
        authorFixtures.setupMultipleTestAuthors(3);

        // Act: Retrieve all authors
        PageResponse<Author> authorsPage = authorEndpoints.getAllAuthorsAndValidateStructure();

        // Assert: Validate the response
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
        // Arrange: Create multiple authors
        authorFixtures.setupMultipleTestAuthors(5);

        // Act: Retrieve authors with custom pagination
        ApiResponse<PageResponse<Author>> response = authorEndpoints.getAllAuthorsWithPagination(0, 10, "name");

        // Assert: Validate the response
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertNotNull(response.getData());

        PageResponse<Author> authorsPage = response.getData();
        assertEquals(0, authorsPage.getNumber()); // First page
        assertEquals(10, authorsPage.getSize()); // Page size
        assertTrue(authorsPage.getTotalElements() >= 5);
    }

    // -------- NEGATIVE TEST SCENARIOS -------- //

    @Test
    @DisplayName("Should fail to create author with invalid data")
    @Description("Test creating an author with invalid data should return error")
    @Story("Author Creation Validation")
    void testCreateAuthorWithInvalidData() {
        // Arrange: Create invalid author request
        AuthorRequest invalidRequest = authorFixtures.createInvalidAuthorRequest();

        // Act: Attempt to create author with invalid data
        ApiResponse<?> errorResponse = authorEndpoints.createAuthorWithInvalidDataAndValidateError(invalidRequest);

        // Assert: Validate error response
        assertNotNull(errorResponse);
        assertFalse(errorResponse.isSuccess());
        assertNotNull(errorResponse.getMessage());
    }

    @Test
    @DisplayName("Should fail to retrieve non-existent author")
    @Description("Test retrieving a non-existent author should return 404 error")
    @Story("Author Retrieval")
    void testGetNonExistentAuthor() {
        // Arrange: Use a non-existent ID
        Long nonExistentId = 999999L;

        // Act: Attempt to retrieve non-existent author
        ApiResponse<?> errorResponse = authorEndpoints.getNonExistentAuthorAndValidateError(nonExistentId);

        // Assert: Validate error response
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
        // Arrange: Create an author
        AuthorRequest authorRequest = authorFixtures.createValidAuthorRequest();

        // Act: Create author and then retrieve it
        Author createdAuthor = authorEndpoints.createAuthorAndValidateStructure(authorRequest);
        Author retrievedAuthor = authorEndpoints.getAuthorByIdAndValidateStructure(createdAuthor.getId());

        // Assert: Data should be consistent
        assertEquals(createdAuthor.getId(), retrievedAuthor.getId());
        assertEquals(createdAuthor.getName(), retrievedAuthor.getName());
        assertEquals(createdAuthor.getBirthDate(), retrievedAuthor.getBirthDate());
        assertEquals(createdAuthor.getNationality(), retrievedAuthor.getNationality());

        // Store for cleanup
        authorFixtures.getCreatedAuthors().add(createdAuthor);
    }
}
