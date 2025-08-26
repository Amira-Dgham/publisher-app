package com.mobelite.e2e.api.endpoints;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mobelite.e2e.api.core.ApiAssertions;
import com.mobelite.e2e.api.core.ApiClient;
import com.mobelite.e2e.api.models.*;
import com.mobelite.e2e.api.models.request.AuthorRequest;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import static com.mobelite.e2e.shared.constants.ApiEndpoints.*;
import static com.mobelite.e2e.shared.constants.HttpStatusCodes.*;

@Slf4j
public class AuthorEndpoints extends BaseEndpoints {

    private static final String API_RESPONSE_SCHEMA = "/schemas/api-response-schema.json";
    private static final String AUTHOR_SCHEMA = "/schemas/author-schema.json";
    private static final String PAGE_RESPONSE_SCHEMA = "/schemas/page-response-schema.json";

    public AuthorEndpoints(ApiClient apiClient) {
        super(apiClient);
    }

    // ---------------------- CREATE ---------------------- //

    @Step("Create author via endpoint")
    public ApiResponse<Author> createAuthor(AuthorRequest authorRequest) {
        return executeRequest(post(AUTHORS_BASE).body(authorRequest), STATUS_CREATED,
                new TypeReference<ApiResponse<Author>>() {});
    }

    @Step("Create author and validate response structure")
    public Author createAuthorAndValidateStructure(AuthorRequest authorRequest) {
        return validateResponseStructure(createAuthor(authorRequest), API_RESPONSE_SCHEMA, AUTHOR_SCHEMA, null,"created successfully");
    }

    // ---------------------- READ ---------------------- //

    @Step("Get all authors via endpoint")
    public ApiResponse<PageResponse<Author>> getAllAuthors() {
        return executeRequest(get(AUTHORS_BASE), STATUS_OK,
                new TypeReference<ApiResponse<PageResponse<Author>>>() {});
    }

    @Step("Get all authors with pagination: page={page}, size={size}, sort={sort}")
    public ApiResponse<PageResponse<Author>> getAllAuthorsWithPagination(int page, int size, String sort) {
        return executeRequest(
                get(AUTHORS_BASE)
                        .queryParam("page", String.valueOf(page))
                        .queryParam("size", String.valueOf(size))
                        .queryParam("sort", sort),
                STATUS_OK,
                new TypeReference<ApiResponse<PageResponse<Author>>>() {}
        );
    }

    @Step("Get author by ID via endpoint: {id}")
    public ApiResponse<Author> getAuthorById(Long id) {
        return executeRequest(get(buildPath(AUTHOR_BY_ID, id)), STATUS_OK,
                new TypeReference<ApiResponse<Author>>() {});
    }

    @Step("Get author by ID and validate structure: {id}")
    public Author getAuthorByIdAndValidateStructure(Long id) {
        // Fixed: First parameter should be API response schema, second should be author schema
        return validateResponseStructure(getAuthorById(id), API_RESPONSE_SCHEMA, AUTHOR_SCHEMA, null,"retrieved successfully");
    }

    @Step("Get all authors and validate structure")
    public PageResponse<Author> getAllAuthorsAndValidateStructure() {
        return validateResponseStructure(
                getAllAuthors(),
                API_RESPONSE_SCHEMA,   // wrapper
                PAGE_RESPONSE_SCHEMA,  // pagination structure
                AUTHOR_SCHEMA,
                "Operation successful"
                // for content items
        );
    }

    // ---------------------- DELETE ---------------------- //

    @Step("Delete author by ID via endpoint: {id}")
    public ApiResponse<Void> deleteAuthor(Long id) {
        return executeRequest(delete(buildPath(AUTHOR_BY_ID, id)), STATUS_OK,
                new TypeReference<ApiResponse<Void>>() {});
    }

    @Step("Delete author by ID and validate structure: {id}")
    public ApiResponse<Void> deleteAuthorAndValidateStructure(Long id) {
        ApiResponse<Void> response = deleteAuthor(id);
        ApiAssertions.assertMessageContains(response, "deleted successfully");
        return response;
    }

    // ---------------------- ERROR SCENARIOS ---------------------- //

    @Step("Create author with invalid data and validate error")
    public ApiResponse<?> createAuthorWithInvalidDataAndValidateError(AuthorRequest invalidRequest) {
        return executeErrorRequest(post(AUTHORS_BASE).body(invalidRequest), STATUS_BAD_REQUEST);
    }

    @Step("Get non-existent author and validate error: {id}")
    public ApiResponse<?> getNonExistentAuthorAndValidateError(Long id) {
        return executeErrorRequest(get(buildPath(AUTHOR_BY_ID, id)), STATUS_NOT_FOUND);
    }

    @Step("Delete non-existent author and validate error: {id}")
    public ApiResponse<?> deleteNonExistentAuthorAndValidateError(Long id) {
        return executeErrorRequest(delete(buildPath(AUTHOR_BY_ID, id)), STATUS_NOT_FOUND);
    }
}