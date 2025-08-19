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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Focused E2E tests for the Author API.
 * Uses a shared author for retrieval tests to avoid creating duplicates.
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

    // Shared author for retrieval tests
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
        // Initialize ApiClient, Endpoints, and Fixtures here
        apiClient = new ApiClient(apiRequestContext);
        authorEndpoints = new AuthorEndpoints(apiClient);
        authorFixtures = new AuthorFixtures(apiClient);

        // Now create the shared author
        AuthorRequest authorRequest = authorFixtures.createSharedAuthorRequest();
        sharedAuthor = authorEndpoints.createAuthorAndValidateStructure(authorRequest);
        authorFixtures.getCreatedAuthors().add(sharedAuthor);
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
        AuthorRequest authorRequest = authorFixtures.createValidAuthorRequest();
        Author createdAuthor = authorEndpoints.createAuthorAndValidateStructure(authorRequest);

        assertNotNull(createdAuthor);
        assertNotNull(createdAuthor.getId());
        assertEquals(authorRequest.getName(), createdAuthor.getName());
        assertEquals(authorRequest.getBirthDate(), createdAuthor.getBirthDate());
        assertEquals(authorRequest.getNationality(), createdAuthor.getNationality());

        authorFixtures.getCreatedAuthors().add(createdAuthor);
    }

    @Test
    @DisplayName("Should create author with minimal data")
    @Description("Test creating an author with only required fields")
    @Story("Author Creation")
    void testCreateAuthorWithMinimalData() {
        AuthorRequest authorRequest = authorFixtures.createMinimalAuthorRequest();
        Author createdAuthor = authorEndpoints.createAuthorAndValidateStructure(authorRequest);

        assertNotNull(createdAuthor);
        assertNotNull(createdAuthor.getId());
        assertEquals(authorRequest.getName(), createdAuthor.getName());

        authorFixtures.getCreatedAuthors().add(createdAuthor);
    }

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

        Author createdAuthor = authorEndpoints.createAuthorAndValidateStructure(authorRequest);
        Author retrievedAuthor = authorEndpoints.getAuthorByIdAndValidateStructure(createdAuthor.getId());

        assertEquals(createdAuthor.getId(), retrievedAuthor.getId());
        assertEquals(createdAuthor.getName(), retrievedAuthor.getName());
        assertEquals(createdAuthor.getBirthDate(), retrievedAuthor.getBirthDate());
        assertEquals(createdAuthor.getNationality(), retrievedAuthor.getNationality());
        authorFixtures.getCreatedAuthors().add(createdAuthor);

    }
}