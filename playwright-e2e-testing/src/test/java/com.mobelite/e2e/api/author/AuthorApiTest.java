package com.mobelite.e2e.api.author;

import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.junit.UsePlaywright;
import com.mobelite.e2e.api.client.ApiClient;
import com.mobelite.e2e.api.client.api.AuthorApiClient;
import com.mobelite.e2e.config.PlaywrightConfig;
import com.mobelite.e2e.fixtures.AuthorFixtures;
import com.mobelite.e2e.models.Author;
import com.mobelite.e2e.models.PageResponse;
import com.mobelite.e2e.models.request.AuthorRequest;
import com.mobelite.e2e.models.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Refactored AuthorApiTest using AuthorFixtures for cleaner test data management.
 */
@UsePlaywright(PlaywrightConfig.class)
public class AuthorApiTest {

    private ApiClient apiClient;
    private AuthorApiClient authorApiClient;
    private AuthorFixtures authorFixtures;

    @BeforeEach
    void setUp(Playwright playwright) {
        apiClient = new ApiClient(playwright);
        authorApiClient = new AuthorApiClient(apiClient);
        authorFixtures = new AuthorFixtures(apiClient);
    }

    @AfterEach
    void tearDown() {
        // Cleanup all authors created during the tests
        authorFixtures.cleanupAllTestAuthors();

        if (apiClient != null) {
            try {
                apiClient.close();
            } catch (Exception e) {
                System.err.println("Failed to close API client: " + e.getMessage());
            }
        }
    }

    @Test
    void testCreateAuthor() {
        // Use fixture to create author and track it automatically
        Author createdAuthor = authorFixtures.createTestAuthorForTest();

        assertNotNull(createdAuthor);
        assertNotNull(createdAuthor.getId());
        assertTrue(createdAuthor.getName().startsWith("TEST_AUTHOR_"));
    }

    @Test
    void testGetAuthorById() {
        Author createdAuthor = authorFixtures.createTestAuthorForTest();

        Author retrievedAuthor = authorApiClient.getAuthorByIdAndValidate(createdAuthor.getId());

        assertNotNull(retrievedAuthor);
        assertEquals(createdAuthor.getId(), retrievedAuthor.getId());
        assertEquals(createdAuthor.getName(), retrievedAuthor.getName());
        assertEquals(createdAuthor.getBirthDate(), retrievedAuthor.getBirthDate());
        assertEquals(createdAuthor.getNationality(), retrievedAuthor.getNationality());
    }

    @Test
    void testGetAllAuthors() {
        // Create multiple authors via fixtures
        List<Author> createdAuthors = authorFixtures.createMultipleTestAuthorsForTest(2);

        PageResponse<Author> authorsPage = authorApiClient.getAllAuthorsAndValidate();

        assertNotNull(authorsPage);
        assertTrue(authorsPage.getTotalElements() >= 2);
        assertTrue(authorsPage.getContent().size() >= 2);

        // Verify that created authors are in the list
        for (Author createdAuthor : createdAuthors) {
            boolean found = authorsPage.getContent().stream()
                    .anyMatch(author -> author.getId().equals(createdAuthor.getId()));
            assertTrue(found, "Created author with ID " + createdAuthor.getId() + " should be in the list");
        }
    }

    @Test
    void testGetAllAuthorsWithPagination() {
        authorFixtures.setupMultipleTestAuthors(5);  // Setup 5 authors for pagination

        ApiResponse<PageResponse<Author>> response = authorApiClient.getAllAuthors(0, 10, "name");

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertNotNull(response.getData());

        PageResponse<Author> authorsPage = response.getData();
        assertNotNull(authorsPage);
        assertNotNull(authorsPage.getContent());
        assertTrue(authorsPage.getContent().size() <= 10);
    }

    @Test
    void testAuthorExists() {
        Author createdAuthor = authorFixtures.createTestAuthorForTest();

        assertTrue(authorApiClient.authorExists(createdAuthor.getId()));
        assertFalse(authorApiClient.authorDoesNotExist(createdAuthor.getId()));
    }

    @Test
    void testAuthorDoesNotExist() {
        Long nonExistentId = 999999L;

        assertFalse(authorApiClient.authorExists(nonExistentId));
        assertTrue(authorApiClient.authorDoesNotExist(nonExistentId));
    }

    @Test
    void testCreateAuthorWithValidation() {
        Author createdAuthor = authorFixtures.createTestAuthorForTest();

        // Validate creation via apiClient helper method
        AuthorRequest request = AuthorRequest.builder()
                .name(createdAuthor.getName())
                .birthDate(createdAuthor.getBirthDate())
                .nationality(createdAuthor.getNationality())
                .build();

        authorApiClient.validateAuthorCreation(request, createdAuthor);
    }

    @Test
    void testCreateAuthorWithMinimalData() {
        AuthorRequest minimalRequest = authorFixtures.createMinimalAuthorRequest();

        Author createdAuthor = authorApiClient.createAuthorAndValidate(minimalRequest);

        assertNotNull(createdAuthor);
        assertNotNull(createdAuthor.getId());
        assertEquals(minimalRequest.getName(), createdAuthor.getName());
    }
}