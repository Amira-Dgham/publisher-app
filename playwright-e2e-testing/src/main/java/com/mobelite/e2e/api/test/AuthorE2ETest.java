package com.mobelite.e2e.api.test;

import com.mobelite.e2e.api.client.ApiClient;
import com.mobelite.e2e.api.endpoints.AuthorEndpoints;
import com.mobelite.e2e.fixtures.AuthorFixtures;
import com.mobelite.e2e.models.Author;
import com.mobelite.e2e.models.ApiResponse;
import com.mobelite.e2e.models.PageResponse;
import com.mobelite.e2e.models.request.AuthorRequest;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive E2E tests for the Author API using AuthorFixtures and AuthorEndpoints.
 * This class demonstrates best practices for E2E testing with proper setup and teardown.
 */
@Epic("Author Management")
@Feature("Author API")
@Story("E2E Testing")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Author API E2E Tests")
public class AuthorE2ETest {

    private ApiClient apiClient;
    private AuthorEndpoints authorEndpoints;
    private AuthorFixtures authorFixtures;

    @BeforeEach
    void setUp() {
        // Initialize the API client with your test configuration
        // Note: In a real test, you would get this from your test configuration
        // apiClient = new ApiClient(playwright);
        // authorEndpoints = new AuthorEndpoints(apiClient);
        // authorFixtures = new AuthorFixtures(apiClient);
        
        log.info("Setting up AuthorE2ETest");
    }

    @AfterEach
    void tearDown() {
        // Clean up test data
        if (authorFixtures != null) {
            authorFixtures.cleanupAllTestAuthors();
        }
        
        // Close the API client
        if (apiClient != null) {
            apiClient.close();
        }
        
        log.info("Tearing down AuthorE2ETest");
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
        // Birth date and nationality are optional, so they might be null
        
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

    @Test
    @DisplayName("Should validate author exists")
    @Description("Test validating that an author exists in the system")
    @Story("Author Validation")
    void testAuthorExists() {
        // Arrange: Create an author
        AuthorRequest authorRequest = authorFixtures.createValidAuthorRequest();
        Author createdAuthor = authorEndpoints.createTestAuthorViaEndpoint(authorRequest);
        authorFixtures.getCreatedAuthors().add(createdAuthor);
        
        // Act & Assert: Verify author exists
        assertTrue(authorEndpoints.authorExists(createdAuthor.getId()));
        assertFalse(authorEndpoints.authorDoesNotExist(createdAuthor.getId()));
    }

    @Test
    @DisplayName("Should create multiple authors with different nationalities")
    @Description("Test creating multiple authors with diverse nationality data")
    @Story("Author Creation")
    void testCreateAuthorsWithDifferentNationalities() {
        // Arrange: Create authors with different nationalities
        authorFixtures.setupAuthorsWithDifferentNationalities();
        
        // Act: Validate all authors exist
        authorFixtures.validateAllCreatedAuthorsExist();
        
        // Assert: Verify the count
        assertEquals(5, authorFixtures.getCreatedAuthorsCount());
        
        // Verify different nationalities
        List<String> nationalities = authorFixtures.getCreatedAuthors().stream()
                .map(Author::getNationality)
                .distinct()
                .collect(Collectors.toList());
        
        assertTrue(nationalities.size() >= 4); // Should have at least 4 different nationalities
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
        assertTrue(errorResponse.getMessage().contains("validation") || 
                  errorResponse.getMessage().contains("error"));
    }

    @Test
    @DisplayName("Should fail to create author with missing required fields")
    @Description("Test creating an author with missing required fields should return error")
    @Story("Author Creation Validation")
    void testCreateAuthorWithMissingRequiredFields() {
        // Arrange: Create incomplete author request
        AuthorRequest incompleteRequest = authorFixtures.createIncompleteAuthorRequest();
        
        // Act: Attempt to create author with missing required fields
        ApiResponse<?> errorResponse = authorEndpoints.createAuthorWithMissingFieldsAndValidateError(incompleteRequest);
        
        // Assert: Validate error response
        assertNotNull(errorResponse);
        assertFalse(errorResponse.isSuccess());
        assertNotNull(errorResponse.getMessage());
        assertTrue(errorResponse.getMessage().contains("validation") || 
                  errorResponse.getMessage().contains("error"));
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

    @Test
    @DisplayName("Should validate author does not exist")
    @Description("Test validating that a non-existent author does not exist in the system")
    @Story("Author Validation")
    void testAuthorDoesNotExist() {
        // Arrange: Use a non-existent ID
        Long nonExistentId = 999999L;
        
        // Act & Assert: Verify author does not exist
        assertFalse(authorEndpoints.authorExists(nonExistentId));
        assertTrue(authorEndpoints.authorDoesNotExist(nonExistentId));
    }

    // -------- EDGE CASE SCENARIOS -------- //

    @Test
    @DisplayName("Should handle large number of authors")
    @Description("Test creating and retrieving a large number of authors")
    @Story("Author Performance")
    void testHandleLargeNumberOfAuthors() {
        // Arrange: Create many authors
        int authorCount = 10;
        authorFixtures.setupMultipleTestAuthors(authorCount);
        
        // Act: Retrieve all authors
        PageResponse<Author> authorsPage = authorEndpoints.getAllAuthorsAndValidateStructure();
        
        // Assert: Validate response
        assertNotNull(authorsPage);
        assertTrue(authorsPage.getTotalElements() >= authorCount);
        assertTrue(authorsPage.hasContent());
        
        // Verify all created authors are in the response
        List<Long> createdIds = authorFixtures.getCreatedAuthors().stream()
                .map(Author::getId)
                .collect(Collectors.toList());
        
        List<Long> responseIds = authorsPage.getContent().stream()
                .map(Author::getId)
                .collect(Collectors.toList());
        
        assertTrue(responseIds.containsAll(createdIds), 
                "All created authors should be present in the response");
    }

    @Test
    @DisplayName("Should handle pagination edge cases")
    @Description("Test pagination with edge case values")
    @Story("Author Pagination")
    void testPaginationEdgeCases() {
        // Arrange: Create some authors
        authorFixtures.setupMultipleTestAuthors(3);
        
        // Act & Assert: Test edge case pagination
        // Page 0 with size 1
        ApiResponse<PageResponse<Author>> response1 = authorEndpoints.getAllAuthorsWithPagination(0, 1, "name");
        assertNotNull(response1);
        assertTrue(response1.isSuccess());
        assertEquals(1, response1.getData().getSize());
        assertTrue(response1.getData().isFirst());
        
        // Page 1 with size 1 (should be empty if only 3 authors exist)
        ApiResponse<PageResponse<Author>> response2 = authorEndpoints.getAllAuthorsWithPagination(1, 1, "name");
        assertNotNull(response2);
        assertTrue(response2.isSuccess());
        assertEquals(1, response2.getData().getSize());
    }

    // -------- INTEGRATION TEST SCENARIOS -------- //

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

    @Test
    @DisplayName("Should handle concurrent author operations")
    @Description("Test creating multiple authors in sequence")
    @Story("Author Concurrency")
    void testConcurrentAuthorOperations() {
        // Arrange: Create multiple author requests
        List<AuthorRequest> requests = authorFixtures.createMultipleAuthorRequests(5);
        List<Author> createdAuthors = new ArrayList<>();
        
        // Act: Create authors sequentially
        for (AuthorRequest request : requests) {
            Author createdAuthor = authorEndpoints.createAuthorAndValidateStructure(request);
            createdAuthors.add(createdAuthor);
            authorFixtures.getCreatedAuthors().add(createdAuthor);
        }
        
        // Assert: All authors should be created successfully
        assertEquals(5, createdAuthors.size());
        
        // Verify all authors exist
        for (Author author : createdAuthors) {
            assertTrue(authorEndpoints.authorExists(author.getId()));
        }
    }
} 