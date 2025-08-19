package com.mobelite.e2e.api.endpoints;

import com.fasterxml.jackson.core.type.TypeReference;
import com.microsoft.playwright.APIResponse;
import com.mobelite.e2e.api.core.ApiClient;
import static com.mobelite.e2e.shared.constants.ApiEndpoints.*;
import static com.mobelite.e2e.shared.constants.HttpStatusCodes.*;
import com.mobelite.e2e.api.models.ApiResponse;
import com.mobelite.e2e.api.models.Author;
import com.mobelite.e2e.api.models.PageResponse;
import com.mobelite.e2e.api.models.request.AuthorRequest;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

/**
 * AuthorEndpoints provides essential endpoint operations for the Author API.
 * Focuses on core CRUD operations and basic validation.
 */
@Slf4j
public class AuthorEndpoints extends BaseEndpoints {

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

        log.info("Creating author via response: {}", response);

        validateStatus(response, STATUS_CREATED);

        // Use TypeReference to properly handle generic types
        return parseResponse(response, new TypeReference<ApiResponse<Author>>() {});
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
        validateIsSuccess(response);

        validateMessageContains(response, "created successfully");
        return response.getData(); // No need for casting anymore
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
        return parseResponse(response, new TypeReference<ApiResponse<Author>>() {});
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
        return parseResponse(response, new TypeReference<ApiResponse<PageResponse<Author>>>() {});
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
        return parseResponse(response, new TypeReference<ApiResponse<PageResponse<Author>>>() {});
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

    /**
     * Deletes an author by their unique identifier.
     *
     * @param id the author ID to delete
     * @return the API response confirming deletion
     */
    @Step("Delete author by ID via endpoint: {id}")
    public ApiResponse<Void> deleteAuthor(Long id) {
        log.info("Deleting author by ID via endpoint: {}", id);

        String endpoint = buildPath(AUTHOR_BY_ID, id);
        APIResponse response = delete(endpoint).execute();

        validateStatus(response, STATUS_OK);
        return parseResponse(response, new TypeReference<ApiResponse<Void>>() {});
    }

    /**
     * Deletes an author by ID and validates the response structure.
     *
     * @param id the author ID to delete
     * @return the delete response
     */
    @Step("Delete author by ID and validate structure: {id}")
    public ApiResponse<Void> deleteAuthorAndValidateStructure(Long id) {
        ApiResponse<Void> response = deleteAuthor(id);
        validateIsSuccess(response);
        validateMessageContains(response, "deleted successfully");
        return response;
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

    /**
     * Attempts to delete a non-existent author and validates the error response.
     *
     * @param id the non-existent author ID
     * @return the error response
     */
    @Step("Delete non-existent author and validate error: {id}")
    public ApiResponse<?> deleteNonExistentAuthorAndValidateError(Long id) {
        log.info("Attempting to delete non-existent author via endpoint: {}", id);

        String endpoint = buildPath(AUTHOR_BY_ID, id);
        APIResponse response = delete(endpoint).execute();

        validateStatus(response, STATUS_NOT_FOUND);
        return parseErrorResponse(response);
    }
}