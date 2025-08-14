package com.mobelite.e2e.api.client.api;

import com.microsoft.playwright.APIResponse;
import com.mobelite.e2e.api.client.ApiClient;
import com.mobelite.e2e.api.endpoints.BaseEndpoints;
import com.mobelite.e2e.models.ApiResponse;
import com.mobelite.e2e.models.Author;
import com.mobelite.e2e.models.PageResponse;
import com.mobelite.e2e.models.request.AuthorRequest;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

/**
 * AuthorApiClient provides methods to interact with the Author API endpoints.
 * Extends BaseEndpoints to leverage common HTTP methods and validation utilities.
 * 
 * Available endpoints:
 * - POST /api/v1/authors - Create a new author
 * - GET /api/v1/authors/{id} - Get author by ID
 * - GET /api/v1/authors - Get all authors with pagination
 */
@Slf4j
public class AuthorApiClient extends BaseEndpoints {

    private static final String BASE_PATH = "/api/v1/authors";

    /**
     * Constructs the AuthorApiClient with a provided ApiClient.
     *
     * @param apiClient the ApiClient instance to be used for sending requests
     */
    public AuthorApiClient(ApiClient apiClient) {
        super(apiClient);
    }

    // -------- CREATE OPERATIONS -------- //

    /**
     * Creates a new author.
     *
     * @param authorRequest the author data to create
     * @return the created author response
     */
    @Step("Create author")
    public ApiResponse<Author> createAuthor(AuthorRequest authorRequest) {
        log.info("Creating author: {}", authorRequest.getName());
        
        APIResponse response = post(BASE_PATH)
                .body(authorRequest)
                .execute();
        
        validateStatus(response, 201);
        return parseResponse(response, ApiResponse.class);
    }

    /**
     * Creates a new author and validates the response.
     *
     * @param authorRequest the author data to create
     * @return the created author
     */
    @Step("Create author and validate response")
    public Author createAuthorAndValidate(AuthorRequest authorRequest) {
        ApiResponse<Author> response = createAuthor(authorRequest);
        validateHasData(response);
        return response.getData();
    }

    // -------- READ OPERATIONS -------- //

    /**
     * Retrieves an author by their ID.
     *
     * @param id the author ID
     * @return the author response
     */
    @Step("Get author by ID: {id}")
    public ApiResponse<Author> getAuthorById(Long id) {
        log.info("Getting author by ID: {}", id);
        
        String endpoint = buildPath(BASE_PATH + "/{id}", id);
        APIResponse response = get(endpoint).execute();
        
        validateStatus(response, 200);
        return parseResponse(response, ApiResponse.class);
    }

    /**
     * Retrieves an author by ID and validates the response.
     *
     * @param id the author ID
     * @return the author
     */
    @Step("Get author by ID and validate: {id}")
    public Author getAuthorByIdAndValidate(Long id) {
        ApiResponse<Author> response = getAuthorById(id);
        validateHasData(response);
        return response.getData();
    }

    /**
     * Retrieves all authors with default pagination.
     *
     * @return the paginated authors response
     */
    @Step("Get all authors")
    public ApiResponse<PageResponse<Author>> getAllAuthors() {
        log.info("Getting all authors");
        
        APIResponse response = get(BASE_PATH).execute();
        
        validateStatus(response, 200);
        return parseResponse(response, ApiResponse.class);
    }

    /**
     * Retrieves all authors with custom pagination parameters.
     *
     * @param page the page number (0-based)
     * @param size the page size
     * @param sort the sort field
     * @return the paginated authors response
     */
    @Step("Get all authors with pagination: page={page}, size={size}, sort={sort}")
    public ApiResponse<PageResponse<Author>> getAllAuthors(int page, int size, String sort) {
        log.info("Getting all authors with pagination: page={}, size={}, sort={}", page, size, sort);
        
        APIResponse response = get(BASE_PATH)
                .queryParam("page", String.valueOf(page))
                .queryParam("size", String.valueOf(size))
                .queryParam("sort", sort)
                .execute();
        
        validateStatus(response, 200);
        return parseResponse(response, ApiResponse.class);
    }

    /**
     * Retrieves all authors and validates the response.
     *
     * @return the paginated authors
     */
    @Step("Get all authors and validate")
    public PageResponse<Author> getAllAuthorsAndValidate() {
        ApiResponse<PageResponse<Author>> response = getAllAuthors();
        validateHasData(response);
        return response.getData();
    }

    // -------- VALIDATION OPERATIONS -------- //

    /**
     * Validates that an author exists by attempting to retrieve it.
     *
     * @param id the author ID to validate
     * @return true if author exists, false otherwise
     */
    @Step("Validate author exists: {id}")
    public boolean authorExists(Long id) {
        try {
            getAuthorById(id);
            return true;
        } catch (AssertionError e) {
            log.debug("Author with ID {} does not exist: {}", id, e.getMessage());
            return false;
        }
    }

    /**
     * Validates that an author does not exist by attempting to retrieve it.
     *
     * @param id the author ID to validate
     * @return true if author does not exist, false otherwise
     */
    @Step("Validate author does not exist: {id}")
    public boolean authorDoesNotExist(Long id) {
        return !authorExists(id);
    }

    /**
     * Validates that the author creation was successful.
     *
     * @param authorRequest the original request
     * @param createdAuthor the created author response
     */
    @Step("Validate author creation")
    public void validateAuthorCreation(AuthorRequest authorRequest, Author createdAuthor) {
        if (createdAuthor == null) {
            throw new AssertionError("Created author is null");
        }
        
        if (createdAuthor.getId() == null) {
            throw new AssertionError("Created author ID is null");
        }
        
        if (!authorRequest.getName().equals(createdAuthor.getName())) {
            throw new AssertionError(String.format(
                "Author name mismatch. Expected: %s, Actual: %s",
                authorRequest.getName(), createdAuthor.getName()
            ));
        }
        
        if (authorRequest.getBirthDate() != null && 
            !authorRequest.getBirthDate().equals(createdAuthor.getBirthDate())) {
            throw new AssertionError(String.format(
                "Author birth date mismatch. Expected: %s, Actual: %s",
                authorRequest.getBirthDate(), createdAuthor.getBirthDate()
            ));
        }
        
        if (authorRequest.getNationality() != null && 
            !authorRequest.getNationality().equals(createdAuthor.getNationality())) {
            throw new AssertionError(String.format(
                "Author nationality mismatch. Expected: %s, Actual: %s",
                authorRequest.getNationality(), createdAuthor.getNationality()
            ));
        }
        
        log.info("Author creation validation passed for ID: {}", createdAuthor.getId());
    }

    // -------- UTILITY OPERATIONS -------- //

    /**
     * Creates a test author and returns the created author data.
     * This is a convenience method for test setup.
     *
     * @param authorRequest the author request data
     * @return the created author
     */
    @Step("Create test author")
    public Author createTestAuthor(AuthorRequest authorRequest) {
        Author createdAuthor = createAuthorAndValidate(authorRequest);
        log.info("Test author created with ID: {}", createdAuthor.getId());
        return createdAuthor;
    }

    /**
     * Cleans up test data by deleting an author.
     * This is a convenience method for test cleanup.
     *
     * @param id the author ID to delete
     */
    @Step("Clean up test author: {id}")
    public void cleanupTestAuthor(Long id) {
        try {
            // Note: Delete endpoint not implemented in current controller
            // This method is prepared for future implementation
            log.info("Test author cleanup prepared for ID: {}", id);
        } catch (Exception e) {
            log.warn("Failed to cleanup test author {}: {}", id, e.getMessage());
        }
    }
}
