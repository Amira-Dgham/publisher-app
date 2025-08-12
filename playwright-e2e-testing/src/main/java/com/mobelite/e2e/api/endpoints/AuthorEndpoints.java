package com.mobelite.e2e.api.endpoints;

import com.microsoft.playwright.APIResponse;
import com.mobelite.e2e.api.client.ApiClient;
import com.mobelite.e2e.api.client.ApiRequestBuilder;
import com.mobelite.e2e.models.ApiResponse;
import com.mobelite.e2e.models.Author;
import com.mobelite.e2e.models.PageResponse;
import com.mobelite.e2e.models.request.AuthorRequest;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

/**
 * AuthorEndpoints provides endpoint-specific operations for the Author API.
 * This class extends BaseEndpoints and provides specialized methods for author operations.
 */
@Slf4j
public class AuthorEndpoints extends BaseEndpoints {

    // API Endpoint paths
    private static final String AUTHORS_BASE = "/api/v1/authors";
    private static final String AUTHOR_BY_ID = AUTHORS_BASE + "/{id}";
    
    // HTTP Status codes
    private static final int STATUS_CREATED = 201;
    private static final int STATUS_OK = 200;
    private static final int STATUS_NOT_FOUND = 404;
    private static final int STATUS_BAD_REQUEST = 400;
    private static final int STATUS_INTERNAL_SERVER_ERROR = 500;

    /**
     * Constructs AuthorEndpoints with the provided ApiClient.
     *
     * @param apiClient the ApiClient instance
     */
    public AuthorEndpoints(ApiClient apiClient) {
        super(apiClient);
    }

    // -------- CREATE ENDPOINTS -------- //

    /**
     * Creates a new author with the provided data.
     *
     * @param authorRequest the author creation request
     * @return the API response containing the created author
     */
    @Step("Create author via endpoint")
    public ApiResponse<Author> createAuthor(AuthorRequest authorRequest) {
        log.info("Creating author via endpoint: {}", authorRequest.getName());
        
        APIResponse response = post(AUTHORS_BASE)
                .body(authorRequest)
                .execute();
        
        validateStatus(response, STATUS_CREATED);
        return parseResponse(response, ApiResponse.class);
    }

    /**
     * Creates a new author and validates the response structure.
     *
     * @param authorRequest the author creation request
     * @return the created author
     */
    @Step("Create author and validate response structure")
    public Author createAuthorAndValidateStructure(AuthorRequest authorRequest) {
        ApiResponse<Author> response = createAuthor(authorRequest);
        validateHasData(response);
        validateMessageContains(response, "created successfully");
        return response.getData();
    }

    // -------- READ ENDPOINTS -------- //

    /**
     * Retrieves an author by their unique identifier.
     *
     * @param id the author ID
     * @return the API response containing the author
     */
    @Step("Get author by ID via endpoint: {id}")
    public ApiResponse<Author> getAuthorById(Long id) {
        log.info("Getting author by ID via endpoint: {}", id);
        
        String endpoint = buildPath(AUTHOR_BY_ID, id);
        APIResponse response = get(endpoint).execute();
        
        validateStatus(response, STATUS_OK);
        return parseResponse(response, ApiResponse.class);
    }

    /**
     * Retrieves an author by ID and validates the response structure.
     *
     * @param id the author ID
     * @return the retrieved author
     */
    @Step("Get author by ID and validate structure: {id}")
    public Author getAuthorByIdAndValidateStructure(Long id) {
        ApiResponse<Author> response = getAuthorById(id);
        validateHasData(response);
        validateMessageContains(response, "retrieved successfully");
        return response.getData();
    }

    /**
     * Retrieves all authors with default pagination.
     *
     * @return the API response containing paginated authors
     */
    @Step("Get all authors via endpoint")
    public ApiResponse<PageResponse<Author>> getAllAuthors() {
        log.info("Getting all authors via endpoint");
        
        APIResponse response = get(AUTHORS_BASE).execute();
        
        validateStatus(response, STATUS_OK);
        return parseResponse(response, ApiResponse.class);
    }

    /**
     * Retrieves all authors with custom pagination parameters.
     *
     * @param page the page number (0-based)
     * @param size the page size
     * @param sort the sort field
     * @return the API response containing paginated authors
     */
    @Step("Get all authors with pagination via endpoint: page={page}, size={size}, sort={sort}")
    public ApiResponse<PageResponse<Author>> getAllAuthorsWithPagination(int page, int size, String sort) {
        log.info("Getting all authors with pagination via endpoint: page={}, size={}, sort={}", page, size, sort);
        
        APIResponse response = get(AUTHORS_BASE)
                .queryParam("page", String.valueOf(page))
                .queryParam("size", String.valueOf(size))
                .queryParam("sort", sort)
                .execute();
        
        validateStatus(response, STATUS_OK);
        return parseResponse(response, ApiResponse.class);
    }

    /**
     * Retrieves all authors and validates the response structure.
     *
     * @return the paginated authors
     */
    @Step("Get all authors and validate structure")
    public PageResponse<Author> getAllAuthorsAndValidateStructure() {
        ApiResponse<PageResponse<Author>> response = getAllAuthors();
        validateHasData(response);
        return response.getData();
    }

    // -------- VALIDATION ENDPOINTS -------- //

    /**
     * Validates that an author exists by attempting to retrieve it.
     *
     * @param id the author ID to validate
     * @return true if author exists, false otherwise
     */
    @Step("Validate author exists via endpoint: {id}")
    public boolean authorExists(Long id) {
        try {
            getAuthorById(id);
            return true;
        } catch (AssertionError e) {
            log.debug("Author with ID {} does not exist via endpoint: {}", id, e.getMessage());
            return false;
        }
    }

    /**
     * Validates that an author does not exist by attempting to retrieve it.
     *
     * @param id the author ID to validate
     * @return true if author does not exist, false otherwise
     */
    @Step("Validate author does not exist via endpoint: {id}")
    public boolean authorDoesNotExist(Long id) {
        return !authorExists(id);
    }

    /**
     * Attempts to retrieve a non-existent author and validates the error response.
     *
     * @param id the non-existent author ID
     * @return the error response
     */
    @Step("Get non-existent author and validate error: {id}")
    public ApiResponse<?> getNonExistentAuthorAndValidateError(Long id) {
        log.info("Attempting to get non-existent author via endpoint: {}", id);
        
        String endpoint = buildPath(AUTHOR_BY_ID, id);
        APIResponse response = get(endpoint).execute();
        
        validateStatus(response, STATUS_NOT_FOUND);
        return parseErrorResponse(response);
    }

    // -------- ERROR SCENARIO ENDPOINTS -------- //

    /**
     * Attempts to create an author with invalid data and validates the error response.
     *
     * @param invalidRequest the invalid author request
     * @return the error response
     */
    @Step("Create author with invalid data and validate error")
    public ApiResponse<?> createAuthorWithInvalidDataAndValidateError(AuthorRequest invalidRequest) {
        log.info("Attempting to create author with invalid data via endpoint");
        
        APIResponse response = post(AUTHORS_BASE)
                .body(invalidRequest)
                .execute();
        
        validateStatus(response, STATUS_BAD_REQUEST);
        return parseErrorResponse(response);
    }

    /**
     * Attempts to create an author with missing required fields and validates the error.
     *
     * @param incompleteRequest the incomplete author request
     * @return the error response
     */
    @Step("Create author with missing required fields and validate error")
    public ApiResponse<?> createAuthorWithMissingFieldsAndValidateError(AuthorRequest incompleteRequest) {
        log.info("Attempting to create author with missing required fields via endpoint");
        
        APIResponse response = post(AUTHORS_BASE)
                .body(incompleteRequest)
                .execute();
        
        validateStatus(response, STATUS_BAD_REQUEST);
        return parseErrorResponse(response);
    }

    // -------- UTILITY ENDPOINTS -------- //

    /**
     * Creates a test author via endpoint and returns the created data.
     * This is a convenience method for test setup.
     *
     * @param authorRequest the author request data
     * @return the created author
     */
    @Step("Create test author via endpoint")
    public Author createTestAuthorViaEndpoint(AuthorRequest authorRequest) {
        Author createdAuthor = createAuthorAndValidateStructure(authorRequest);
        log.info("Test author created via endpoint with ID: {}", createdAuthor.getId());
        return createdAuthor;
    }

    /**
     * Validates the author creation response structure.
     *
     * @param authorRequest the original request
     * @param createdAuthor the created author response
     */
    @Step("Validate author creation response structure")
    public void validateAuthorCreationResponse(AuthorRequest authorRequest, Author createdAuthor) {
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
        
        log.info("Author creation response validation passed for ID: {}", createdAuthor.getId());
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