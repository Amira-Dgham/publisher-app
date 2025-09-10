package com.mobelite.e2e.api.endpoints;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mobelite.e2e.api.core.BaseApiEndPoint;
import com.mobelite.e2e.api.models.*;
import com.mobelite.e2e.api.models.request.AuthorRequest;
import lombok.extern.slf4j.Slf4j;

import static com.mobelite.e2e.shared.constants.ApiEndpoints.*;

@Slf4j
public class AuthorApiEndPoint extends BaseApiEndPoint<Author, AuthorRequest> {

    @Override
    protected String getEntityName() { return "Author"; }

    @Override
    protected String getItemSchema() { return "/schemas/author-schema.json"; }

    @Override
    protected TypeReference<ApiResponse<Author>> getItemTypeReference() { return new TypeReference<>() {}; }

    @Override
    protected TypeReference<ApiResponse<PageResponse<Author>>> getPageTypeReference() { return new TypeReference<>() {}; }

    // -------- Convenience wrappers --------
    public PageResponse<Author> getAllAuthors() {
        return getAllAndValidate(AUTHORS_BASE);
    }

    // Default method: tracks created author
    public Author createAuthor(AuthorRequest request) {
        return createAuthor(request, true);
    }

    // Overloaded method: optional tracking
    public Author createAuthor(AuthorRequest request, boolean trackForCleanup) {
        Author author = createAndValidate(request, AUTHORS_BASE);
        if (trackForCleanup) {
            trackForCleanup(author.getId());
        }
        return author;
    }

    public Author getAuthorById(Long id) {
        return getByIdAndValidate(id, AUTHOR_BY_ID);
    }

    public ApiResponse<Void> deleteAuthor(Long id) {
        return deleteAndValidate(id, AUTHOR_BY_ID);
    }

    public Author getByName(String name) {
        String searchEndpoint = AUTHORS_BASE + "?name=" + name;
        PageResponse<Author> page = getAllAndValidate(searchEndpoint);
        if (page.getContent().isEmpty()) return null;
        return page.getContent().get(0);
    }

    // -------- Request builders for negative/error cases --------

    public ApiResponse<?> getNonExistentAuthor(Long id,int expectedStatus) {
        return executeInvalidGet(id, AUTHOR_BY_ID, expectedStatus);
    }

    public ApiResponse<?> createInvalidAuthor(AuthorRequest request, int expectedStatus) {
        return executeInvalidPost(request, AUTHORS_BASE, expectedStatus);
    }

    public ApiResponse<?> deleteNonExistentAuthor(Long id, int expectedStatus) {
        return executeInvalidDelete(id, AUTHOR_BY_ID, expectedStatus);
    }
}