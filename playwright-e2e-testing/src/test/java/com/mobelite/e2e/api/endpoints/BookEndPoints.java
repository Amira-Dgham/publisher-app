package com.mobelite.e2e.api.endpoints;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mobelite.e2e.api.core.ApiAssertions;
import com.mobelite.e2e.api.core.ApiClient;
import com.mobelite.e2e.api.models.*;
import com.mobelite.e2e.api.models.request.BookRequest;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import static com.mobelite.e2e.shared.constants.ApiEndpoints.*;
import static com.mobelite.e2e.shared.constants.HttpStatusCodes.*;

@Slf4j
public class BookEndPoints extends BaseEndpoints {

    private static final String API_RESPONSE_SCHEMA = "/schemas/api-response-schema.json";
    private static final String BOOK_SCHEMA = "/schemas/book-schema.json";
    private static final String PAGE_RESPONSE_SCHEMA = "/schemas/page-response-schema.json";

    public BookEndPoints(ApiClient apiClient) {
        super(apiClient);
    }

    // ---------------------- CREATE ---------------------- //

    @Step("Create book via endpoint")
    public ApiResponse<Book> createBook(BookRequest bookRequest) {
        return executeRequest(post(BOOKS_BASE).body(bookRequest), STATUS_CREATED,
                new TypeReference<ApiResponse<Book>>() {});
    }

    @Step("Create book and validate response structure")
    public Book createBookAndValidateStructure(BookRequest bookRequest) {
        return validateResponseStructure(createBook(bookRequest), API_RESPONSE_SCHEMA, BOOK_SCHEMA, null,"created successfully");
    }

    // ---------------------- READ ---------------------- //

    @Step("Get all books via endpoint")
    public ApiResponse<PageResponse<Book>> getAllBooks() {
        return executeRequest(get(BOOKS_BASE), STATUS_OK,
                new TypeReference<ApiResponse<PageResponse<Book>>>() {});
    }

    @Step("Get all books with pagination: page={page}, size={size}, sort={sort}")
    public ApiResponse<PageResponse<Book>> getAllBooksWithPagination(int page, int size, String sort) {
        return executeRequest(
                get(BOOKS_BASE)
                        .queryParam("page", String.valueOf(page))
                        .queryParam("size", String.valueOf(size))
                        .queryParam("sort", sort),
                STATUS_OK,
                new TypeReference<ApiResponse<PageResponse<Book>>>() {}
        );
    }

    @Step("Get book by ID via endpoint: {id}")
    public ApiResponse<Book> getBookById(Long id) {
        return executeRequest(get(buildPath(BOOKS_BY_ID, id)), STATUS_OK,
                new TypeReference<ApiResponse<Book>>() {});
    }

    @Step("Get book by ID and validate structure: {id}")
    public Book getBookByIdAndValidateStructure(Long id) {
        return validateResponseStructure(getBookById(id), API_RESPONSE_SCHEMA, BOOK_SCHEMA, null,"retrieved successfully");
    }

    @Step("Get all books and validate structure")
    public PageResponse<Book> getAllBooksAndValidateStructure() {
        return validateResponseStructure(
                getAllBooks(),
                API_RESPONSE_SCHEMA,   // wrapper
                PAGE_RESPONSE_SCHEMA,  // pagination structure
                BOOK_SCHEMA,           // for content items
                "Operation successful"
        );
    }

    @Step("Get books by author ID via endpoint: {authorId}")
    public ApiResponse<PageResponse<Book>> getBooksByAuthorId(Long authorId) {
        return executeRequest(
                get(BOOKS_BASE)
                        .queryParam("authorId", String.valueOf(authorId)),
                STATUS_OK,
                new TypeReference<ApiResponse<PageResponse<Book>>>() {}
        );
    }

    @Step("Get books by author ID and validate structure: {authorId}")
    public PageResponse<Book> getBooksByAuthorIdAndValidateStructure(Long authorId) {
        return validateResponseStructure(
                getBooksByAuthorId(authorId),
                API_RESPONSE_SCHEMA,   // wrapper
                PAGE_RESPONSE_SCHEMA,  // pagination structure
                BOOK_SCHEMA,           // for content items
                "Operation successful"
        );
    }

    // ---------------------- UPDATE ---------------------- //

    @Step("Update book by ID via endpoint: {id}")
    public ApiResponse<Book> updateBook(Long id, BookRequest bookRequest) {
        return executeRequest(put(buildPath(BOOKS_BY_ID, id)).body(bookRequest), STATUS_OK,
                new TypeReference<ApiResponse<Book>>() {});
    }

    @Step("Update book by ID and validate structure: {id}")
    public Book updateBookAndValidateStructure(Long id, BookRequest bookRequest) {
        return validateResponseStructure(updateBook(id, bookRequest), API_RESPONSE_SCHEMA, BOOK_SCHEMA, null,"updated successfully");
    }

    // ---------------------- DELETE ---------------------- //

    @Step("Delete book by ID via endpoint: {id}")
    public ApiResponse<Void> deleteBook(Long id) {
        return executeRequest(delete(buildPath(BOOKS_BY_ID, id)), STATUS_OK,
                new TypeReference<ApiResponse<Void>>() {});
    }

    @Step("Delete book by ID and validate structure: {id}")
    public ApiResponse<Void> deleteBookAndValidateStructure(Long id) {
        ApiResponse<Void> response = deleteBook(id);
        ApiAssertions.assertMessageContains(response, "deleted successfully");
        return response;
    }

    // ---------------------- ERROR SCENARIOS ---------------------- //

    @Step("Create book with invalid data and validate error")
    public ApiResponse<?> createBookWithInvalidDataAndValidateError(BookRequest invalidRequest) {
        return executeErrorRequest(post(BOOKS_BASE).body(invalidRequest), STATUS_BAD_REQUEST);
    }

    @Step("Get non-existent book and validate error: {id}")
    public ApiResponse<?> getNonExistentBookAndValidateError(Long id) {
        return executeErrorRequest(get(buildPath(BOOKS_BY_ID, id)), STATUS_NOT_FOUND);
    }

    @Step("Delete non-existent book and validate error: {id}")
    public ApiResponse<?> deleteNonExistentBookAndValidateError(Long id) {
        return executeErrorRequest(delete(buildPath(BOOKS_BY_ID, id)), STATUS_NOT_FOUND);
    }

    @Step("Update non-existent book and validate error: {id}")
    public ApiResponse<?> updateNonExistentBookAndValidateError(Long id, BookRequest bookRequest) {
        return executeErrorRequest(put(buildPath(BOOKS_BY_ID, id)).body(bookRequest), STATUS_NOT_FOUND);
    }

    @Step("Create book with non-existent author and validate error")
    public ApiResponse<?> createBookWithNonExistentAuthorAndValidateError(BookRequest bookRequest) {
        return executeErrorRequest(post(BOOKS_BASE).body(bookRequest), STATUS_BAD_REQUEST);
    }
}