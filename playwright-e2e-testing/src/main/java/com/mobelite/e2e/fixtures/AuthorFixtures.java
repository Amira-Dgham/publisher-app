package com.mobelite.e2e.fixtures;

import com.mobelite.e2e.api.client.ApiClient;
import com.mobelite.e2e.api.endpoints.AuthorEndpoints;
import com.mobelite.e2e.models.Author;
import com.mobelite.e2e.models.request.AuthorRequest;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * AuthorFixtures provides test data and setup/teardown methods for author E2E testing.
 * This class manages test data lifecycle and provides reusable test scenarios.
 */
@Slf4j
public class AuthorFixtures {

    private final AuthorEndpoints authorEndpoints;
    private final List<Author> createdAuthors;
    private final AtomicLong authorIdCounter;
    
    // Test data constants
    private static final String TEST_AUTHOR_PREFIX = "TEST_AUTHOR_";
    private static final int MAX_TEST_AUTHORS = 10;

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

    // -------- TEST DATA GENERATION -------- //

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

    /**
     * Creates an author request with missing required fields.
     *
     * @return an incomplete AuthorRequest
     */
    @Step("Create incomplete author request")
    public AuthorRequest createIncompleteAuthorRequest() {
        return AuthorRequest.builder()
                .birthDate(LocalDate.of(1990, 5, 15))
                .nationality("British")
                .build();
        // Missing name field
    }

    /**
     * Creates multiple valid author requests for bulk testing.
     *
     * @param count the number of author requests to create
     * @return a list of AuthorRequest objects
     */
    @Step("Create {count} valid author requests")
    public List<AuthorRequest> createMultipleAuthorRequests(int count) {
        List<AuthorRequest> requests = new ArrayList<>();
        for (int i = 0; i < count && i < MAX_TEST_AUTHORS; i++) {
            requests.add(createValidAuthorRequest());
        }
        return requests;
    }

    /**
     * Creates author requests with different nationalities for diversity testing.
     *
     * @return a list of AuthorRequest objects with different nationalities
     */
    @Step("Create author requests with different nationalities")
    public List<AuthorRequest> createAuthorsWithDifferentNationalities() {
        List<AuthorRequest> requests = new ArrayList<>();
        
        requests.add(AuthorRequest.builder()
                .name(TEST_AUTHOR_PREFIX + "American_" + authorIdCounter.getAndIncrement())
                .birthDate(LocalDate.of(1980, 1, 1))
                .nationality("American")
                .build());
                
        requests.add(AuthorRequest.builder()
                .name(TEST_AUTHOR_PREFIX + "British_" + authorIdCounter.getAndIncrement())
                .birthDate(LocalDate.of(1985, 5, 15))
                .nationality("British")
                .build());
                
        requests.add(AuthorRequest.builder()
                .name(TEST_AUTHOR_PREFIX + "French_" + authorIdCounter.getAndIncrement())
                .birthDate(LocalDate.of(1975, 8, 22))
                .nationality("French")
                .build());
                
        requests.add(AuthorRequest.builder()
                .name(TEST_AUTHOR_PREFIX + "German_" + authorIdCounter.getAndIncrement())
                .birthDate(LocalDate.of(1990, 12, 10))
                .nationality("German")
                .build());
                
        requests.add(AuthorRequest.builder()
                .name(TEST_AUTHOR_PREFIX + "Japanese_" + authorIdCounter.getAndIncrement())
                .birthDate(LocalDate.of(1982, 3, 20))
                .nationality("Japanese")
                .build());
        
        return requests;
    }

    // -------- TEST SETUP AND TEARDOWN -------- //

    /**
     * Sets up test data by creating a single test author.
     * This method is typically called in @BeforeEach.
     */
    @Step("Setup single test author")
    public void setupSingleTestAuthor() {
        AuthorRequest request = createValidAuthorRequest();
        Author createdAuthor = authorEndpoints.createTestAuthorViaEndpoint(request);
        createdAuthors.add(createdAuthor);
        log.info("Setup: Created test author with ID: {}", createdAuthor.getId());
    }

    /**
     * Sets up test data by creating multiple test authors.
     * This method is typically called in @BeforeEach.
     *
     * @param count the number of authors to create
     */
    @Step("Setup {count} test authors")
    public void setupMultipleTestAuthors(int count) {
        List<AuthorRequest> requests = createMultipleAuthorRequests(count);
        
        for (AuthorRequest request : requests) {
            Author createdAuthor = authorEndpoints.createTestAuthorViaEndpoint(request);
            createdAuthors.add(createdAuthor);
            log.info("Setup: Created test author with ID: {}", createdAuthor.getId());
        }
    }

    /**
     * Sets up test data with authors of different nationalities.
     * This method is typically called in @BeforeEach.
     */
    @Step("Setup authors with different nationalities")
    public void setupAuthorsWithDifferentNationalities() {
        List<AuthorRequest> requests = createAuthorsWithDifferentNationalities();
        
        for (AuthorRequest request : requests) {
            Author createdAuthor = authorEndpoints.createTestAuthorViaEndpoint(request);
            createdAuthors.add(createdAuthor);
            log.info("Setup: Created test author with ID: {} and nationality: {}", 
                    createdAuthor.getId(), createdAuthor.getNationality());
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
                authorEndpoints.cleanupTestAuthor(author.getId());
                log.info("Cleanup: Successfully cleaned up author with ID: {}", author.getId());
            } catch (Exception e) {
                log.warn("Cleanup: Failed to cleanup author with ID {}: {}", author.getId(), e.getMessage());
            }
        }
        
        createdAuthors.clear();
        log.info("Cleanup: Completed cleanup of all test authors");
    }

    /**
     * Cleans up a specific test author.
     *
     * @param authorId the ID of the author to cleanup
     */
    @Step("Cleanup specific test author: {authorId}")
    public void cleanupSpecificTestAuthor(Long authorId) {
        try {
            authorEndpoints.cleanupTestAuthor(authorId);
            createdAuthors.removeIf(author -> author.getId().equals(authorId));
            log.info("Cleanup: Successfully cleaned up author with ID: {}", authorId);
        } catch (Exception e) {
            log.warn("Cleanup: Failed to cleanup author with ID {}: {}", authorId, e.getMessage());
        }
    }

    // -------- TEST SCENARIO HELPERS -------- //

    /**
     * Creates a test author and returns the created data.
     * This is a convenience method for individual test methods.
     *
     * @return the created author
     */
    @Step("Create test author for individual test")
    public Author createTestAuthorForTest() {
        AuthorRequest request = createValidAuthorRequest();
        Author createdAuthor = authorEndpoints.createTestAuthorViaEndpoint(request);
        createdAuthors.add(createdAuthor);
        log.info("Individual test: Created author with ID: {}", createdAuthor.getId());
        return createdAuthor;
    }

    /**
     * Creates multiple test authors and returns the created data.
     * This is a convenience method for individual test methods.
     *
     * @param count the number of authors to create
     * @return a list of created authors
     */
    @Step("Create {count} test authors for individual test")
    public List<Author> createMultipleTestAuthorsForTest(int count) {
        List<AuthorRequest> requests = createMultipleAuthorRequests(count);
        List<Author> createdAuthors = new ArrayList<>();
        
        for (AuthorRequest request : requests) {
            Author createdAuthor = authorEndpoints.createTestAuthorViaEndpoint(request);
            createdAuthors.add(createdAuthor);
            this.createdAuthors.add(createdAuthor);
            log.info("Individual test: Created author with ID: {}", createdAuthor.getId());
        }
        
        return createdAuthors;
    }

    /**
     * Validates that all created authors exist in the system.
     * This is useful for verifying test setup was successful.
     */
    @Step("Validate all created authors exist")
    public void validateAllCreatedAuthorsExist() {
        log.info("Validation: Validating that {} created authors exist", createdAuthors.size());
        
        for (Author author : createdAuthors) {
            boolean exists = authorEndpoints.authorExists(author.getId());
            if (!exists) {
                throw new AssertionError(String.format(
                    "Author with ID %d does not exist in the system", author.getId()
                ));
            }
            log.info("Validation: Author with ID {} exists", author.getId());
        }
        
        log.info("Validation: All {} created authors exist in the system", createdAuthors.size());
    }

    /**
     * Gets the list of all created authors.
     *
     * @return the list of created authors
     */
    public List<Author> getCreatedAuthors() {
        return new ArrayList<>(createdAuthors);
    }

    /**
     * Gets the count of created authors.
     *
     * @return the count of created authors
     */
    public int getCreatedAuthorsCount() {
        return createdAuthors.size();
    }

    /**
     * Checks if any authors have been created.
     *
     * @return true if authors exist, false otherwise
     */
    public boolean hasCreatedAuthors() {
        return !createdAuthors.isEmpty();
    }
} 