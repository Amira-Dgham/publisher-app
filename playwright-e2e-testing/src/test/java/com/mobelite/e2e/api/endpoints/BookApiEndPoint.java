package com.mobelite.e2e.api.endpoints;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mobelite.e2e.api.core.BaseApiEndPoint;
import com.mobelite.e2e.api.models.*;
import com.mobelite.e2e.api.models.request.BookRequest;
import com.mobelite.e2e.api.models.response.ApiResponse;
import com.mobelite.e2e.api.models.response.PageResponse;

import static com.mobelite.e2e.shared.constants.ApiEndpoints.*;
import static com.mobelite.e2e.shared.constants.HttpStatusCodes.STATUS_CREATED;
import static com.mobelite.e2e.shared.constants.HttpStatusCodes.STATUS_OK;

public class BookApiEndPoint extends BaseApiEndPoint<Book, BookRequest> {

    @Override
    protected String getEntityName() { return "Book"; }

    @Override
    protected String getItemSchema() { return "/schemas/book-schema.json"; }

    @Override
    protected TypeReference<ApiResponse<Book>> getItemTypeReference() {
        return new TypeReference<>() {};
    }

    @Override
    protected TypeReference<ApiResponse<PageResponse<Book>>> getPageTypeReference() {
        return new TypeReference<>() {};
    }

    // --- Convenience wrappers ---
    public Book createBook(BookRequest request) {
        return createBook(request, true);
    }

    // Overloaded method: optional tracking
    public Book createBook(BookRequest request, boolean trackForCleanup) {
        Book book = createAndValidate(request, BOOKS_BASE,STATUS_CREATED);
        if (trackForCleanup) {
            trackForCleanup(book.getId());
        }
        return book;
    }

    public Book getBookById(Long id) {
        return getByIdAndValidate(id, BOOKS_BY_ID,STATUS_OK);
    }

    public PageResponse<Book> getAllBooks() {
        return getAllAndValidate(BOOKS_BASE,STATUS_OK);
    }

    public ApiResponse<Void> deleteBook(Long id) {
        return deleteAndValidate(id, BOOKS_BY_ID,STATUS_OK);
    }


    public ApiResponse<?> createInvalidBook(BookRequest request, int expectedStatus) {
        return executeInvalidPost(request, BOOKS_BASE, expectedStatus);
    }

    public ApiResponse<?> deleteNonExistentBook(Long id, int expectedStatus) {
        return executeInvalidDelete(id, BOOKS_BY_ID, expectedStatus);
    }
}